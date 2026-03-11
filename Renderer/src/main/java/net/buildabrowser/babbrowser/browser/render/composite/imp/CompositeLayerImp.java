package net.buildabrowser.babbrowser.browser.render.composite.imp;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayerEntry;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;

public class CompositeLayerImp implements CompositeLayer {

  private final List<CompositeLayer> childLayers = new LinkedList<>();

  private final PositionValue positioning;
  private final float offsetX;
  private final float offsetY;
  private final int zIndex;

  // Unfortunately can't use the LayoutFragment's intrusive list, as it is already in use
  private CompositeLayerEntry entries;

  private boolean sorted = false;

  public CompositeLayerImp(
    PositionValue positioning,
    float offsetX, float offsetY,
    int zIndex
  ) {
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
  }

  @Override
  public void paint(PaintCanvas canvas) {
    if (!sorted) {
      // Ideally, we would use a set that stays sorted (like a TreeSet) but is stable (like LinkedHashSet).
      // Unfortunately, that does not exist in the standard libraries.
      Collections.sort(childLayers, (a, b) -> Integer.compare(a.zIndex(), b.zIndex()));
      sorted = true;
    }

    forEachFragment(fragment -> fragment.painter().paintBackground(fragment, canvas), canvas);
    for (CompositeLayer layer: childLayers) {
      if (layer.zIndex() >= 0) continue;
      paintChildLayer(canvas, layer);
    }
    forEachFragment(fragment -> {
      canvas.alterPaint(p -> p.incOffset(
        fragment.contentX() - fragment.borderX(),
        fragment.contentY() - fragment.borderY()));
      fragment.painter().paint(fragment, canvas);
    }, canvas);
    for (CompositeLayer layer: childLayers) {
      if (layer.zIndex() < 0) continue;
      paintChildLayer(canvas, layer);
    }
  }

  private void forEachFragment(Consumer<BoxFragment> func, PaintCanvas canvas) {
    CompositeLayerEntry nextEntry = entries;
    while (nextEntry != null) {
      canvas.pushPaint();
      CompositeLayerEntry currentEntry = nextEntry;
      nextEntry = nextEntry.next();
      canvas.alterPaint(p -> p.incOffset(currentEntry.offsetX(), currentEntry.offsetY()));
      func.accept(currentEntry.fragment());
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

  private void paintChildLayer(PaintCanvas canvas, CompositeLayer layer) {
    canvas.pushPaint();
    canvas.alterPaint(paint -> paint.incOffset(layer.posX(), layer.posY()));
    layer.paint(canvas);
    canvas.popPaint();
  }
  
}
