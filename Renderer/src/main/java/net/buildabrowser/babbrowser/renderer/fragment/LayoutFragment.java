package net.buildabrowser.babbrowser.renderer.fragment;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;

public abstract class LayoutFragment implements IntrusiveList<LayoutFragment> {
  
  private final float width;
  private final float height;

  private LayoutFragment nextFragment;
  private float posX = Float.NaN;
  private float posY = Float.NaN;
  private float layerX = Float.NaN;
  private float layerY = Float.NaN;

  public LayoutFragment(float width, float height) {
    this.width = width;
    this.height = height;
  }

  @Override
  public LayoutFragment next() {
    return this.nextFragment;
  }

  @Override
  public void setNext(LayoutFragment nextFragment) {
    this.nextFragment = nextFragment;
    assert IntrusiveList._ensureNoLoops(nextFragment);
  }

  public void setPos(float x, float y) {
    this.posX = x;
    this.posY = y;
  }

  public float posX(Measurement type) {
    assert !Float.isNaN(this.posX);
    return this.posX;
  }

  public float posY(Measurement type) {
    assert !Float.isNaN(this.posX);
    return this.posY;
  }

  public void setLayerPos(float layerX, float layerY) {
    this.layerX = layerX;
    this.layerY = layerY;
  }

  public float layerX(Measurement type) {
    assert !Float.isNaN(this.layerX);
    return this.layerX;
  }

  public float layerY(Measurement type) {
    assert !Float.isNaN(this.layerY);
    return this.layerY;
  }

  public float width(Measurement type) {
    return this.width;
  }

  public float inkWidth(Measurement type) {
    return this.width;
  }

  public float height(Measurement type) {
    return this.height;
  }

  public float inkHeight(Measurement type) {
    return this.height;
  }

  // Measured from box top
  public float firstBaseline(Measurement type) {
    return 0;
  }

  // Measured from box bottom
  public float lastBaseline(Measurement type) {
    return 0;
  }

  public static enum Measurement {
    MARGIN, BORDER, PADDING, CONTENT
  }

}
