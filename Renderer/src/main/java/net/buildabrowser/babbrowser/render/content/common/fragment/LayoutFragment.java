package net.buildabrowser.babbrowser.render.content.common.fragment;

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

  public float marginX() {
    assert this.posX != Float.NaN : "Attempt to get unset X position!";
    return this.posX;
  }

  public float marginY() {
    assert this.posY != Float.NaN : "Attempt to get unset Y position!";
    return this.posY;
  }

  public float borderX() {
    assert this.posX != Float.NaN : "Attempt to get unset X position!";
    return this.posX;
  }

  public float borderY() {
    assert this.posY != Float.NaN : "Attempt to get unset Y position!";
    return this.posY;
  }

  public float contentX() {
    return borderX();
  }

  public float contentY() {
    return borderY();
  }

  public float marginWidth() {
    return this.width;
  }

  public float marginHeight() {
    return this.height;
  }

  public float borderWidth() {
    return this.width;
  }

  public float borderHeight() {
    return this.height;
  }

  public float contentWidth() {
    return this.width;
  }

  public float contentHeight() {
    return this.height;
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
