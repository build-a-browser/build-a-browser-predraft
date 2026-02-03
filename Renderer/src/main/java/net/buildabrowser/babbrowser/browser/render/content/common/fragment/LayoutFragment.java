package net.buildabrowser.babbrowser.browser.render.content.common.fragment;

public abstract class LayoutFragment {
  
  private final float width;
  private final float height;

  private float posX = -1;
  private float posY = -1;

  public LayoutFragment(float width, float height) {
    this.width = width;
    this.height = height;
  }

  public void setPos(float x, float y) {
    this.posX = x;
    this.posY = y;
  }

  public void setParent(LayoutFragment parent) {
    // No-op, subclasses can override
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

  // Since LineBoxFragment and BoxFragment do not share another common parent, the layerpos methods are here for convenience
  public float layerX() {
    throw new UnsupportedOperationException("This fragment does not support layer coordinates!");
  }

  public float layerY() {
    throw new UnsupportedOperationException("This fragment does not support layer coordinates!");
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
