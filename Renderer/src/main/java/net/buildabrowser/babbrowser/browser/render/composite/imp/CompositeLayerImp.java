package net.buildabrowser.babbrowser.browser.render.composite.imp;

import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.position.PositionValue;

public class CompositeLayerImp implements CompositeLayer {

  private final List<CompositeLayer> childLayers = new LinkedList<>();

  private final CompositeLayer parent;
  private final BoxFragment rootFragment;

  private int offsetX = 0;
  private int offsetY = 0;

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
      childLayers.add(layer);
    }
  }

  @Override
  public PositionValue positioning() {
    return (PositionValue) rootFragment.box().activeStyles().getProperty(CSSProperty.POSITION);
  }

  @Override
  public boolean isPassthrough() {
    return false;
  }

  @Override
  public void paint(PaintCanvas canvas) {
    rootFragment.box().content().paintBackground(canvas);
    rootFragment.box().content().paint(canvas);
    for (CompositeLayer layer: childLayers) {
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
    return rootFragment.paintOffsetX() + offsetX;
  }

  @Override
  public int posY() {
    return rootFragment.paintOffsetY() + offsetY;
  }

  private void paintChildLayer(PaintCanvas canvas, CompositeLayer layer) {
    canvas.pushPaint();
    canvas.alterPaint(paint -> paint.incOffset(layer.posX(), layer.posY()));
    layer.paint(canvas);
    canvas.popPaint();
  }
  
}
