package net.buildabrowser.babbrowser.render.composite.imp;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;

import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.render.composite.CompositeLayerEntry;
import net.buildabrowser.babbrowser.render.composite.LayerBitMap;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;
import net.buildabrowser.babbrowser.render.paint.backend.Painter;

public class CompositeLayerImp implements CompositeLayer {

  private static final int OVERSCROLL_FACTOR = 3;

  private final List<CompositeLayer> childLayers = new LinkedList<>();

  private final PositionValue positioning;
  private final float offsetX, offsetY;
  private final int zIndex;

  private final LayerBitMap foregroundBitMap;

  // Unfortunately can't use the LayoutFragment's intrusive list, as it is already in use
  private CompositeLayerEntry entries;
  private int backingWidth, backingHeight;
  private boolean sorted;

  public CompositeLayerImp(
    Painter painter,
    PositionValue positioning,
    float offsetX, float offsetY,
    int zIndex
  ) {
    this.positioning = positioning;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.zIndex = zIndex;

    this.foregroundBitMap = new LayerBitMapImp(painter);
  }

  @Override
  public void addChild(CompositeLayer childLayer) {
    sorted = false;
    childLayers.add(childLayer);
  }

  @Override
  public void addEntries(CompositeLayerEntry entries) {
    this.entries = entries;
    this.backingWidth = backingWidth();
    this.backingHeight = backingHeight();
  }

  @Override
  public void repaint(int[] vpIntersection) {
    ensureLayersSorted();

    ScrollBoxFragment scrollBoxFragment = relatedScrollBox();

    // Trying to avoid a new array or record allocation, there's already enough short-lived objects as-is
    int vX = vpIntersection[0], vY = vpIntersection[1], vW = vpIntersection[2], vH = vpIntersection[3];

    int scrollX = scrollBoxFragment == null ? 0 : -scrollBoxFragment.box().scrollX();
    int scrollY = scrollBoxFragment == null ? 0 : -scrollBoxFragment.box().scrollY();
    vpIntersection[0] -= this.offsetX + scrollX;
    vpIntersection[1] -= this.offsetY + scrollY;

    repaintSelf(vpIntersection, scrollBoxFragment);

    for (CompositeLayer childLayer: childLayers) {
      childLayer.repaint(vpIntersection);
    }

    vpIntersection[0] = vX; vpIntersection[1] = vY; vpIntersection[2] = vW; vpIntersection[3] = vH;
  }

  private void repaintSelf(int[] vpIntersection, ScrollBoxFragment scrollBoxFragment) {
    int vX = vpIntersection[0], vY = vpIntersection[1], vW = vpIntersection[2], vH = vpIntersection[3];
    
    int overscrollWidth = Math.min(backingWidth, vW * OVERSCROLL_FACTOR);
    int overscrollHeight = Math.min(backingHeight, vH * OVERSCROLL_FACTOR);
    int overscrollXUnclamped = vpIntersection[0] - Math.max(0, (overscrollWidth - vW) / 2);
    int overscrollYUnclamped = vpIntersection[1] - Math.max(0, (overscrollHeight - vH) / 2);

    int nearbyViewportWidth = vpIntersection[2] * OVERSCROLL_FACTOR;
    int nearbyViewportHeight = vpIntersection[3] * OVERSCROLL_FACTOR;
    int nearbyViewportX = vpIntersection[0] - Math.max(0, (nearbyViewportWidth - vW) / 2);
    int nearbyViewportY = vpIntersection[1] - Math.max(0, (nearbyViewportHeight - vH) / 2);
    if (
      nearbyViewportX + nearbyViewportWidth < 0
      || nearbyViewportX > backingWidth
      || nearbyViewportY + nearbyViewportHeight < 0
      || nearbyViewportY > backingHeight
    ) {
      foregroundBitMap.resize(0, 0, 0, 0);
      foregroundBitMap.update(_ -> {});
      return;
    }

    int overscrollX = Math.clamp(overscrollXUnclamped, 0, backingWidth - overscrollWidth);
    int overscrollY = Math.clamp(overscrollYUnclamped, 0, backingHeight - overscrollHeight);

    vpIntersection[0] = overscrollX; vpIntersection[1] = overscrollY; vpIntersection[2] = overscrollWidth; vpIntersection[3] = overscrollHeight;

    foregroundBitMap.resize(overscrollX, overscrollY, overscrollWidth, overscrollHeight);
    foregroundBitMap.update(canvas -> {
      // TODO: These paint checks aren't cool
      if (entries != null) {
        canvas.alterPaint(p -> p.setFont(entries.fragment().box().layoutContext().font()));
      }
      forEachFragment((fragment, vpi) -> {
        canvas.alterPaint(p -> p.incOffset(
          fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER),
          fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER)));

        fragment.painter().paint(fragment, canvas, vpi);
      }, canvas, vpIntersection);
    });


    vpIntersection[0] = vX; vpIntersection[1] = vY; vpIntersection[2] = vW; vpIntersection[3] = vH;
  }

  @Override
  public void draw(PaintCanvas canvas, int[] vpIntersection) {
    if (entries != null) {
      canvas.alterPaint(p -> p.setFont(entries.fragment().box().layoutContext().font()));
    }

    int vX = vpIntersection[0], vY = vpIntersection[1], vW = vpIntersection[2], vH = vpIntersection[3];

    // TODO: Having to treat scrollable entries specially is not great
    ScrollBoxFragment scrollBoxFragment = relatedScrollBox();
    int scrollX = scrollBoxFragment == null ? 0 : -scrollBoxFragment.box().scrollX();
    int scrollY = scrollBoxFragment == null ? 0 : -scrollBoxFragment.box().scrollY();

    vpIntersection[0] -= this.offsetX + scrollX;
    vpIntersection[1] -= this.offsetY + scrollY;

    forEachFragment(
      (fragment, vpi) -> fragment.painter().paintBackground(fragment, canvas, vpi),
      canvas, vpIntersection);
    
    for (CompositeLayer layer: childLayers) {
      if (layer.zIndex() >= 0) continue;
      paintChildLayer(canvas, layer, scrollX, scrollY, vpIntersection);
    }

    // Parent already offset the x, y by layer.
    foregroundBitMap.draw(scrollX, scrollY, canvas);
    
    for (CompositeLayer layer: childLayers) {
      if (layer.zIndex() < 0) continue;
      paintChildLayer(canvas, layer, scrollX, scrollY, vpIntersection);
    }

    if (scrollBoxFragment != null) {
      vpIntersection[0] += scrollX;
      vpIntersection[1] += scrollY;

      canvas.pushPaint();
      canvas.alterPaint(p -> p.incOffset(entries.offsetX(), entries.offsetY()));
      scrollBoxFragment.painter().paintScrollbars(scrollBoxFragment, canvas);
      canvas.popPaint();
    }

    vpIntersection[0] = vX; vpIntersection[1] = vY; vpIntersection[2] = vW; vpIntersection[3] = vH;
  }

  @Override
  public PositionValue positioning() {
    return this.positioning;
  }

  @Override
  public float posX() {
    return offsetX;
  }

  @Override
  public float posY() {
    return offsetY;
  }

  @Override
  public int zIndex() {
    return this.zIndex;
  }

  @Override
  public List<CompositeLayer> childLayers() {
    ensureLayersSorted();

    return this.childLayers;
  }

  @Override
  public CompositeLayerEntry entries() {
    return this.entries;
  }

  private ScrollBoxFragment relatedScrollBox() {
    if (
      entries != null
      && entries.next() == null
      && entries.fragment() instanceof ScrollBoxFragment scrollBox
    ) return scrollBox;

    return null;
  }

  private void ensureLayersSorted() {
    if (!sorted) {
      // Ideally, we would use a set that stays sorted (like a TreeSet) but is stable (like LinkedHashSet).
      // Unfortunately, that does not exist in the standard libraries.
      Collections.sort(childLayers, (a, b) -> Integer.compare(a.zIndex(), b.zIndex()));
      sorted = true;
    }
  }

  private void forEachFragment(
    BiConsumer<BoxFragment, int[]> func, PaintCanvas canvas, int[] vpIntersection
  ) {
    CompositeLayerEntry nextEntry = entries;
    while (nextEntry != null) {
      CompositeLayerEntry currentEntry = nextEntry;
      nextEntry = nextEntry.next();

      BoxFragment fragment = currentEntry.fragment();
      if (fragment.painter() == null) continue;

      vpIntersection[0] -= currentEntry.offsetX();
      vpIntersection[1] -= currentEntry.offsetY();
      
      canvas.pushPaint();
      canvas.alterPaint(p -> p.incOffset(
        currentEntry.offsetX(),
        currentEntry.offsetY()));

      // Technically you can use the outer vpIntersection since it's the same array
      // but this feels cleaner
      func.accept(fragment, vpIntersection);

      vpIntersection[0] += currentEntry.offsetX();
      vpIntersection[1] += currentEntry.offsetY();
      
      canvas.popPaint();
    }
  }

  private void paintChildLayer(
    PaintCanvas canvas, CompositeLayer layer,
    float scrollX, float scrollY,
    int[] vpIntersection
  ) {
    canvas.pushPaint();
    canvas.alterPaint(paint -> paint.incOffset(
      layer.posX() + scrollX,
      layer.posY() + scrollY));
    layer.draw(canvas, vpIntersection);
    canvas.popPaint();
  }

  private int backingWidth() {
    float minX = Integer.MAX_VALUE;
    float maxX = Integer.MIN_VALUE;
    CompositeLayerEntry currentEntry = entries;
    while (currentEntry != null) {
      BoxFragment fragment = currentEntry.fragment();
      float adjustedWidth = fragment.inkWidth(Measurement.PADDING);
      minX = Math.min(minX, currentEntry.offsetX());
      maxX = Math.max(maxX, currentEntry.offsetX() + adjustedWidth);
      currentEntry = currentEntry.next();
    }

    return (int) Math.ceil(Math.max(0, maxX - minX));
  }

  private int backingHeight() {
    float minY = Integer.MAX_VALUE;
    float maxY = Integer.MIN_VALUE;
    CompositeLayerEntry currentEntry = entries;
    while (currentEntry != null) {
      BoxFragment fragment = currentEntry.fragment();
      float adjustedHeight = fragment.inkHeight(Measurement.PADDING);
      minY = Math.min(minY, currentEntry.offsetY());
      maxY = Math.max(maxY, currentEntry.offsetY() + adjustedHeight);
      currentEntry = currentEntry.next();
    }

    return (int) Math.ceil(Math.max(0, maxY - minY));
  }
  
}
