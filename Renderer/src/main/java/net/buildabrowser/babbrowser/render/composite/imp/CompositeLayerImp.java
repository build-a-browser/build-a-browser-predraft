package net.buildabrowser.babbrowser.render.composite.imp;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.render.composite.CompositeLayerEntry;
import net.buildabrowser.babbrowser.render.composite.LayerBitMap;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.render.content.scroll.ScrollContentPainter;
import net.buildabrowser.babbrowser.render.paint.PaintCanvas;
import net.buildabrowser.babbrowser.render.paint.Painter;

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
  public void repaint(int[] viewport) {
    ensureLayersSorted();

    ScrollBoxFragment scrollBoxFragment = relatedScrollBox();
    int scrollX = scrollBoxFragment == null ? 0 : -scrollBoxFragment.box().scrollX();
    int scrollY = scrollBoxFragment == null ? 0 : -scrollBoxFragment.box().scrollY();

    // Trying to avoid a new array allocation
    int vX = viewport[0], vY = viewport[1], vW = viewport[2], vH = viewport[3];

    viewport[0] -= this.offsetX + scrollX;
    viewport[1] -= this.offsetY + scrollY;

    repaintSelf(viewport, scrollBoxFragment);

    for (CompositeLayer childLayer: childLayers) {
      childLayer.repaint(viewport);
    }

    viewport[0] = vX; viewport[1] = vY; viewport[2] = vW; viewport[3] = vH;
  }

  private void repaintSelf(int[] viewport, ScrollBoxFragment scrollBoxFragment) {
    int vW = viewport[2], vH = viewport[3];
    int overscrollWidth = Math.min(backingWidth, vW * OVERSCROLL_FACTOR);
    int overscrollHeight = Math.min(backingHeight, vH * OVERSCROLL_FACTOR);
    int overscrollXUnclamped = viewport[0] - Math.max(0, (overscrollWidth - vW) / 2);
    int overscrollYUnclamped = viewport[1] - Math.max(0, (overscrollHeight - vH) / 2);

    int nearbyViewportWidth = viewport[2] * OVERSCROLL_FACTOR;
    int nearbyViewportHeight = viewport[3] * OVERSCROLL_FACTOR;
    int nearbyViewportX = viewport[0] - Math.max(0, (nearbyViewportWidth - vW) / 2);
    int nearbyViewportY = viewport[1] - Math.max(0, (nearbyViewportHeight - vH) / 2);
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

    foregroundBitMap.resize(overscrollX, overscrollY, overscrollWidth, overscrollHeight);
    foregroundBitMap.update(canvas -> {
      // TODO: These paint checks aren't cool
      if (entries != null) {
        canvas.alterPaint(p -> p.setFont(entries.fragment().box().layoutContext().font()));
      }

      if (scrollBoxFragment != null) {
        ScrollContentPainter.paintForeground(scrollBoxFragment, canvas);
      } else {
        forEachFragment(fragment -> {
          canvas.alterPaint(p -> p.incOffset(
            fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER),
            fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER)));
          fragment.painter().paint(fragment, canvas);
        }, canvas);
      }
    });
  }

  @Override
  public void draw(PaintCanvas canvas) {
    if (entries != null) {
      canvas.alterPaint(p -> p.setFont(entries.fragment().box().layoutContext().font()));
    }

    // TODO: Having to treat scrollable entries specially is not great
    ScrollBoxFragment scrollBoxFragment = relatedScrollBox();
    int scrollX = scrollBoxFragment == null ? 0 : -scrollBoxFragment.box().scrollX();
    int scrollY = scrollBoxFragment == null ? 0 : -scrollBoxFragment.box().scrollY();
    if (scrollBoxFragment != null) {
      canvas.pushPaint();
      canvas.alterPaint(p -> p.incOffset(
        entries.offsetX() + scrollX,
        entries.offsetY() + scrollY));
      ScrollContentPainter.paintBackground(scrollBoxFragment, canvas);
      canvas.popPaint();
    } else {
      // TODO: When support for things like opacity is added
      // a temporary bitmap may be needed to squash background/foreground
      forEachFragment(fragment -> fragment.painter().paintBackground(fragment, canvas), canvas);
    }
    
    for (CompositeLayer layer: childLayers) {
      if (layer.zIndex() >= 0) continue;
      paintChildLayer(canvas, layer, scrollX, scrollY);
    }

    // Parent already offset the x, y by layer.
    foregroundBitMap.draw(scrollX, scrollY, canvas);
    
    for (CompositeLayer layer: childLayers) {
      if (layer.zIndex() < 0) continue;
      paintChildLayer(canvas, layer, scrollX, scrollY);
    }

    if (scrollBoxFragment != null) {
      canvas.pushPaint();
      canvas.alterPaint(p -> p.incOffset(entries.offsetX(), entries.offsetY()));
      entries.fragment().painter().paint(scrollBoxFragment, canvas);
      canvas.popPaint();
    }
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
    Consumer<BoxFragment> func, PaintCanvas canvas
  ) {
    CompositeLayerEntry nextEntry = entries;
    while (nextEntry != null) {
      CompositeLayerEntry currentEntry = nextEntry;
      nextEntry = nextEntry.next();
      canvas.pushPaint();
      // TODO: Figure out why pre-layout fragments are making it into composite layers
      assert !(currentEntry.fragment() instanceof ScrollBoxFragment);
      if (currentEntry.fragment().painter() == null) return;
      canvas.alterPaint(p -> p.incOffset(
        currentEntry.offsetX(),
        currentEntry.offsetY()));
      func.accept(currentEntry.fragment());
      canvas.popPaint();
    }
  }

  private void paintChildLayer(
    PaintCanvas canvas, CompositeLayer layer,
    float scrollX, float scrollY
  ) {
    canvas.pushPaint();
    canvas.alterPaint(paint -> paint.incOffset(
      layer.posX() + scrollX,
      layer.posY() + scrollY));
    layer.draw(canvas);
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
