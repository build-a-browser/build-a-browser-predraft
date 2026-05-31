package net.buildabrowser.babbrowser.renderer.content.common.fragment;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;

public abstract class LayoutFragment implements IntrusiveList<LayoutFragment> {
  
  private final float width;
  private final float height;

  private LayoutFragment nextFragment;
  private float posX = Float.NaN;
  private float posY = Float.NaN;

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

  public static enum Measurement {
    MARGIN, BORDER, PADDING, CONTENT
  }

}
