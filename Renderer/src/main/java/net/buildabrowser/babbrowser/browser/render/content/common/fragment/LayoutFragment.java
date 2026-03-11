package net.buildabrowser.babbrowser.browser.render.content.common.fragment;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;

public abstract class LayoutFragment implements IntrusiveList<LayoutFragment> {
  
  private final float width;
  private final float height;

  private LayoutFragment nextFragment;
  private float posX = -1;
  private float posY = -1;

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
  }

  public void setPos(float x, float y) {
    this.posX = x;
    this.posY = y;
  }

  public float marginX() {
    assert this.posX != -1 : "Attempt to get unset X position!";
    return this.posX;
  }

  public float marginY() {
    assert this.posY != -1 : "Attempt to get unset Y position!";
    return this.posY;
  }

  public float borderX() {
    assert this.posX != -1 : "Attempt to get unset X position!";
    return this.posX;
  }

  public float borderY() {
    assert this.posY != -1 : "Attempt to get unset Y position!";
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

}
