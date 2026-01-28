package net.buildabrowser.babbrowser.browser.render.composite.imp;

import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.browser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;
import net.buildabrowser.babbrowser.css.engine.property.position.PositionValue;

public class RootCompositeLayerImp implements CompositeLayer {

  private final List<CompositeLayer> childLayers = new LinkedList<>();

  private final int width, height;

  public RootCompositeLayerImp(int width, int height) {
    this.width = width;
    this.height = height;
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
    childLayers.add(layer);
  }

  @Override
  public PositionValue positioning() {
    return PositionValue.STATIC;
  }

  @Override
  public boolean isPassthrough() {
    return false;
  }

  @Override
  public void paint(PaintCanvas canvas) {
    for (CompositeLayer child: childLayers) {
      child.paint(canvas);
    }
  }

  @Override
  public void incOffset(int offsetX, int offsetY) {
    throw new UnsupportedOperationException("Cannot offset root layer!");
  }

  @Override
  public int posX() {
    return 0;
  }

  @Override
  public int posY() {
    return 0;
  }

  @Override
  public int width() {
    return this.width;
  }

  @Override
  public int height() {
    return this.height;
  }

  @Override
  public int zIndex() {
    return 0;
  }
  
}
