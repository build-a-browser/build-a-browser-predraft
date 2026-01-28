package net.buildabrowser.babbrowser.browser.render.composite.imp;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.position.PositionValue;
import net.buildabrowser.babbrowser.css.engine.property.position.ZIndexValue;

public class CompositeLayerImp implements CompositeLayer {

  private final List<CompositeLayer> childLayers = new LinkedList<>();

  private final CompositeLayer parent;
  private final BoxFragment rootFragment;

  private int offsetX = 0;
  private int offsetY = 0;
  private boolean sorted = false;

  public CompositeLayerImp(CompositeLayer parent, BoxFragment rootFragment) {
    this.parent = parent;
    this.rootFragment = rootFragment;
  }

  @Override
  public List<CompositeLayer> childLayers() {
    return this.childLayers;
  }

  @Override
  public CompositeLayer createChild(BoxFragment childBoxFragment) {
    CompositeLayer childLayer = new CompositeLayerImp(this, childBoxFragment);
    addChildLayer(childLayer);

    return childLayer;
  }

  @Override
  public void addChildLayer(CompositeLayer layer) {
    if (isPassthrough()) {
      layer.incOffset(posX(), posY());
      parent.addChildLayer(layer);
    } else {
      sorted = false;
      childLayers.add(layer);
    }
  }

  @Override
  public PositionValue positioning() {
    return (PositionValue) rootFragment.box().activeStyles().getProperty(CSSProperty.POSITION);
  }

  @Override
  public boolean isPassthrough() {
    return
      positioning().equals(PositionValue.RELATIVE)
      && rootFragment.box().activeStyles().getProperty(CSSProperty.Z_INDEX).equals(CSSValue.AUTO);
  }

  @Override
  public void paint(PaintCanvas canvas) {
    if (!sorted) {
      // Ideally, we would use a set that stays sorted (like a TreeSet) but is stable (like LinkedHashSet).
      // Unfortunately, that does not exist in the standard libraries.
      Collections.sort(childLayers, (a, b) -> Integer.compare(a.zIndex(), b.zIndex()));
      sorted = true;
    }
    rootFragment.box().content().paintBackground(canvas);
    for (CompositeLayer layer: childLayers) {
      if (layer.zIndex() >= 0) continue;
      paintChildLayer(canvas, layer);
    }
    rootFragment.box().content().paint(canvas);
    for (CompositeLayer layer: childLayers) {
      if (layer.zIndex() < 0) continue;
      paintChildLayer(canvas, layer);
    }
  }

  @Override
  public void incOffset(int offsetX, int offsetY) {
    this.offsetX += offsetX;
    this.offsetY += offsetY;
  }

  @Override
  public int posX() {
    return offsetX;
  }

  @Override
  public int posY() {
    return offsetY;
  }

  @Override
  public int width() {
    return rootFragment.borderWidth();
  }

  @Override
  public int height() {
    return rootFragment.borderHeight();
  }

  @Override
  public int zIndex() {
    CSSValue zIndexValue = rootFragment.box().activeStyles().getProperty(CSSProperty.Z_INDEX);
    return zIndexValue.equals(CSSValue.AUTO) ? 0 : ((ZIndexValue) zIndexValue).zIndex();
  }

  private void paintChildLayer(PaintCanvas canvas, CompositeLayer layer) {
    canvas.pushPaint();
    canvas.alterPaint(paint -> paint.incOffset(layer.posX(), layer.posY()));
    layer.paint(canvas);
    canvas.popPaint();
  }
  
}
