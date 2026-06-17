package net.buildabrowser.babbrowser.renderer.composite.imp;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.mathClamp;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.painter.core.PaintBitMap;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayer;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayerEntry;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;

public class CompositeLayerImp implements CompositeLayer {

  private static final int OVERSCROLL_FACTOR = 3;

  private final List<CompositeLayer> childLayers = new ArrayList<>(1);
  private final BitSet activeChildren = new BitSet();

  private final Painter backingPainter;
  private final PositionValue positioning;
  private final float offsetX, offsetY;
  private final int zIndex;

  // Unfortunately can't use the LayoutFragment's intrusive list, as it is already in use
  private CompositeLayerEntry entries;
  private int backingWidth, backingHeight;
  private int backingX, backingY;
  private PaintBitMap backingImage;
  private boolean sorted;

  public CompositeLayerImp(
    Painter painter,
    PositionValue positioning,
    float offsetX, float offsetY,
    int zIndex
  ) {
    this.backingPainter = painter;
    this.positioning = positioning;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.zIndex = zIndex;
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
  public void repaint(VpIntersection vpIntersection) {
    ensureLayersSorted();

    ScrollBoxFragment scrollBoxFragment = relatedScrollBox();
    int scrollX = scrollBoxFragment == null ? 0 : scrollBoxFragment.scrollX();
    int scrollY = scrollBoxFragment == null ? 0 : scrollBoxFragment.scrollY();

    vpIntersection.enterOffset(
      this.offsetX, this.offsetY, scrollX, scrollY,
      vpi -> repaintAll(vpi, scrollBoxFragment));
  }

  private void repaintAll(VpIntersection vpIntersection, ScrollBoxFragment scrollBoxFragment) {
    repaintSelf(vpIntersection, scrollBoxFragment);

    activeChildren.clear();
    int i = 0;
    for (CompositeLayer childLayer: childLayers) {
      childLayer.repaint(vpIntersection);
      activeChildren.set(i++, childLayer.layerActive());
    }
  }

  // This math is so confusing (╥﹏╥) - I spent hours trying to get it right
  // yPast is the height of the portion of the layer already above the viewport, or 0 if it is not above the viewport
  // overscrollYUnclamped adds a half buffer above yPast
  // overscrollY ensures the overflow buffer does not start before the top edge of the layer
  // enterBuffer takes coordinates adjusted to be relative to the viewport
  // makeBackingImage just takes the element-relative coordinates - the code does its own adjustment for scroll later
  
  // Please don't nest enterBuffer calls, it will make my life a million times harder
  // Hopefully I got it right, also there are no unit tests b/c this is graphics code
  private void repaintSelf(VpIntersection vpIntersection, ScrollBoxFragment scrollBoxFragment) {
    int overscrollWidth = Math.min(backingWidth, vpIntersection.vpWidth() * OVERSCROLL_FACTOR);
    int overscrollHeight = Math.min(backingHeight, vpIntersection.vpHeight() * OVERSCROLL_FACTOR);
    
    int xPast = Math.max(0, -vpIntersection.elVpX());
    int yPast = Math.max(0, -vpIntersection.elVpY());
    
    int overscrollXUnclamped = xPast - Math.max(0, (overscrollWidth - vpIntersection.vpWidth()) / 2);
    int overscrollYUnclamped = yPast - Math.max(0, (overscrollHeight - vpIntersection.vpHeight()) / 2);
    
    int overscrollX = mathClamp(overscrollXUnclamped, 0, backingWidth - overscrollWidth);
    int overscrollY = mathClamp(overscrollYUnclamped, 0, backingHeight - overscrollHeight);
    
    float vpOverscrollX = vpIntersection.elVpX() + overscrollX;
    float vpOverscrollY = vpIntersection.elVpY() + overscrollY;

    int nearbyViewportWidth = vpIntersection.vpWidth() * OVERSCROLL_FACTOR;
    int nearbyViewportHeight = vpIntersection.vpHeight() * OVERSCROLL_FACTOR;
    int nearbyViewportX = -Math.max(0, (nearbyViewportWidth - vpIntersection.vpWidth()) / 2);
    int nearbyViewportY = -Math.max(0, (nearbyViewportHeight - vpIntersection.vpHeight()) / 2);
    if (
      nearbyViewportX + nearbyViewportWidth < vpOverscrollX
      || nearbyViewportX > vpOverscrollX + backingWidth
      || nearbyViewportY + nearbyViewportHeight < vpOverscrollY
      || nearbyViewportY > vpOverscrollY + backingHeight
    ) {
      this.backingImage = null;
      return;
    }

    vpIntersection.enterBuffer(
      vpOverscrollX, vpOverscrollY,
      overscrollWidth, overscrollHeight,
      vpi -> makeBackingImage(vpi, overscrollWidth, overscrollHeight, overscrollX, overscrollY));
  }

  private void makeBackingImage(
    VpIntersection vpIntersection,
    int overscrollWidth, int overscrollHeight,
    int overscrollX, int overscrollY
  ) {
    this.backingImage = backingPainter.createPaintBitMap(
      // TODO: Hack for it to work when content is above layer start (e.g. negative margin)
      // But this probably won't work with fixed-size layers
      Math.max(overscrollWidth, 1),
      Math.max(overscrollHeight, 1));
    this.backingX = overscrollX;
    this.backingY = overscrollY;

    backingImage.withCanvas(canvas -> canvas.saveTransform(c -> {
      // TODO: These paint checks aren't cool
      if (entries == null) return;
      forEachFragment((fragment, vpi) -> {
        c.withTransform(
          t -> t.translate(
            fragment.posX(Measurement.CONTENT) - fragment.posX(Measurement.BORDER) - backingX,
            fragment.posY(Measurement.CONTENT) - fragment.posY(Measurement.BORDER) - backingY),
          c2 -> fragment.withPainterV((p, f) -> p.paint(f, c2, vpi)));
      }, canvas, vpIntersection);
    }));
  }

  @Override
  public void draw(PaintCanvas canvas, VpIntersection vpIntersection) {
    if (entries != null) {
      canvas.withPaint(
        p -> p.setFont(entries.fragment().box().layoutContext().font()),
        c -> drawMaybeScrollable(c, vpIntersection));
    } else {
      drawMaybeScrollable(canvas, vpIntersection);
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

  @Override
  public boolean layerActive() {
    return backingImage != null || !activeChildren.isEmpty();
  }

  private void drawMaybeScrollable(PaintCanvas canvas, VpIntersection vpIntersection) {
    // TODO: Having to treat scrollable entries specially is not great
    ScrollBoxFragment scrollBoxFragment = relatedScrollBox();

    vpIntersection.enterOffset(
      this.offsetX, this.offsetY, 0, 0,
      vpi -> {
        if (scrollBoxFragment == null) {
          drawInnerContent(canvas, vpIntersection, scrollBoxFragment, 0, 0);
        } else {
          drawScrollable(canvas, vpIntersection, scrollBoxFragment);
        }
      });
  }

  private void drawScrollable(PaintCanvas canvas, VpIntersection vpIntersection, ScrollBoxFragment scrollBoxFragment) {
    int scrollX = scrollBoxFragment == null ? 0 : scrollBoxFragment.scrollX();
    int scrollY = scrollBoxFragment == null ? 0 : scrollBoxFragment.scrollY();

    vpIntersection.enterOffset(
      0, 0, scrollX, scrollY,
      vpi -> canvas.withClip(
        scrollBoxFragment.posX(Measurement.CONTENT) - scrollBoxFragment.posX(Measurement.BORDER),
        scrollBoxFragment.posY(Measurement.CONTENT) - scrollBoxFragment.posY(Measurement.BORDER),
        scrollBoxFragment.width(Measurement.CONTENT),
        scrollBoxFragment.height(Measurement.CONTENT),
        c -> drawInnerContent(c, vpIntersection, scrollBoxFragment, scrollX, scrollY)));

    canvas.withTransform(
      t -> t.translate(entries.offsetX(), entries.offsetY()),
      c -> scrollBoxFragment.painter().paintScrollbars(scrollBoxFragment, c));
  }

  private void drawInnerContent(
    PaintCanvas canvas,
    VpIntersection vpIntersection,
    ScrollBoxFragment scrollBoxFragment,
    int scrollX, int scrollY
  ) {
    if (scrollBoxFragment != null) {
      scrollBoxFragment.withPainterV((p, f) -> p.paintBackground(f, canvas, vpIntersection));
    } else {
      forEachFragment(
        (fragment, vpi) -> fragment.withPainterV((p, f) -> p.paintBackground(f, canvas, vpi)),
        canvas, vpIntersection);
    }
    
    int gtLayerStart = 0;
    for (int i = activeChildren.nextSetBit(0); i >= 0; i = activeChildren.nextSetBit(i + 1)) {
      CompositeLayer layer = childLayers.get(i);
      if (layer.zIndex() >= 0) {
        gtLayerStart = i;
        break;
      }
      paintChildLayer(canvas, layer, scrollX, scrollY, vpIntersection);
    }

    // Parent already offset the x, y by layer.
    if (this.backingImage != null) {
      canvas.drawBitMap(this.backingX - scrollX, this.backingY - scrollY, backingImage);
    }
    
    for (int i = activeChildren.nextSetBit(gtLayerStart); i >= 0; i = activeChildren.nextSetBit(i + 1)) {
      CompositeLayer layer = childLayers.get(i);
      paintChildLayer(canvas, layer, scrollX, scrollY, vpIntersection);
    }
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
    BiConsumer<BoxFragment<?>, VpIntersection> func, PaintCanvas canvas, VpIntersection vpIntersection
  ) {
    CompositeLayerEntry nextEntry = entries;
    while (nextEntry != null) {
      CompositeLayerEntry currentEntry = nextEntry;
      nextEntry = nextEntry.next();

      BoxFragment<?> fragment = currentEntry.fragment();
      
      vpIntersection.enterOffset(
        currentEntry.offsetX(), currentEntry.offsetY(), 0, 0,
        vpi -> canvas.withTransform(
          t -> t.translate(currentEntry.offsetX(), currentEntry.offsetY()),
          // TODO: While canvas and _1 are the same right now, this may need refactored in the future
          _1 -> func.accept(fragment, vpi)
        ));
    }
  }

  private void paintChildLayer(
    PaintCanvas canvas, CompositeLayer layer,
    float scrollX, float scrollY,
    VpIntersection vpIntersection
  ) {
    if (this.backingImage == null) return;
    canvas.withTransform(
      t -> t.translate(
        layer.posX() - scrollX,
        layer.posY() - scrollY),
      c -> layer.draw(c, vpIntersection));
  }

  private int backingWidth() {
    float minX = Integer.MAX_VALUE;
    float maxX = Integer.MIN_VALUE;
    CompositeLayerEntry currentEntry = entries;
    while (currentEntry != null) {
      BoxFragment<?> fragment = currentEntry.fragment();
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
      BoxFragment<?> fragment = currentEntry.fragment();
      float adjustedHeight = fragment.inkHeight(Measurement.PADDING);
      minY = Math.min(minY, currentEntry.offsetY());
      maxY = Math.max(maxY, currentEntry.offsetY() + adjustedHeight);
      currentEntry = currentEntry.next();
    }

    return (int) Math.ceil(Math.max(0, maxY - minY));
  }
  
}
