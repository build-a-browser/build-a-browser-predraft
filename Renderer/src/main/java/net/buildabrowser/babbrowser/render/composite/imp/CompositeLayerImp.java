package net.buildabrowser.babbrowser.render.composite.imp;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.render.composite.CompositeLayerEntry;
import net.buildabrowser.babbrowser.render.composite.imp.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.render.composite.imp.scroll.ScrollContentPainter;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.paint.PaintCanvas;

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
    // TODO: Having to do this is not great
    if (entries != null && entries.next() == null) {
      paintSingle(canvas);
      return;
    }

    ensureLayersSorted();

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

  private void paintSingle(PaintCanvas canvas) {
    BoxFragment fragment = entries.fragment();
    ScrollBoxFragment scrollBox = fragment instanceof ScrollBoxFragment scrollBox2 ? scrollBox2 : null;

    canvas.pushPaint();
    canvas.alterPaint(p -> p.incOffset(entries.offsetX(), entries.offsetY()));
    if (scrollBox == null) {
      fragment.painter().paintBackground(fragment, canvas);
    } else {
      ScrollContentPainter.paintBackground(scrollBox, canvas);
    }
    canvas.popPaint();

    for (CompositeLayer layer: childLayers) {
      if (layer.zIndex() >= 0) continue;
      paintChildLayer(canvas, layer);
    }

    canvas.pushPaint();
    canvas.alterPaint(p -> p.incOffset(
      entries.offsetX() + fragment.contentX() - fragment.borderX(),
      entries.offsetY() + fragment.contentY() - fragment.borderY()));
    if (scrollBox == null) {
      fragment.painter().paint(fragment, canvas);
    } else {
      ScrollContentPainter.paintForeground(scrollBox, canvas);
    }
    canvas.popPaint();
    
    for (CompositeLayer layer: childLayers) {
      if (layer.zIndex() < 0) continue;
      paintChildLayer(canvas, layer);
    }

    if (scrollBox != null) {
      canvas.pushPaint();
      canvas.alterPaint(p -> p.incOffset(entries.offsetX(), entries.offsetY()));
      ScrollContentPainter.paint(scrollBox, canvas);
      canvas.popPaint();
    }
  }

  private void ensureLayersSorted() {
    if (!sorted) {
      // Ideally, we would use a set that stays sorted (like a TreeSet) but is stable (like LinkedHashSet).
      // Unfortunately, that does not exist in the standard libraries.
      Collections.sort(childLayers, (a, b) -> Integer.compare(a.zIndex(), b.zIndex()));
      sorted = true;
    }
  }

  private void forEachFragment(Consumer<BoxFragment> func, PaintCanvas canvas) {
    CompositeLayerEntry nextEntry = entries;
    while (nextEntry != null) {
      CompositeLayerEntry currentEntry = nextEntry;
      nextEntry = nextEntry.next();
      canvas.pushPaint();
      // TODO: Figure out why pre-layout fragments are making it into composite layers
      assert !(currentEntry.fragment() instanceof ScrollBoxFragment);
      if (currentEntry.fragment().painter() == null) return;
      canvas.alterPaint(p -> p.incOffset(currentEntry.offsetX(), currentEntry.offsetY()));
      func.accept(currentEntry.fragment());
      canvas.popPaint();
    }
  }

  private void paintChildLayer(PaintCanvas canvas, CompositeLayer layer) {
    canvas.pushPaint();
    canvas.alterPaint(paint -> paint.incOffset(layer.posX(), layer.posY()));
    layer.paint(canvas);
    canvas.popPaint();
  }
  
}
