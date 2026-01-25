package net.buildabrowser.babbrowser.browser.render.content.flow.fragment;

public abstract class FlowFragment {
  
  private final int width;
  private final int height;

  private int posX = -1;
  private int posY = -1;

  public FlowFragment(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void setPos(int x, int y) {
    this.posX = x;
    this.posY = y;
  }

  public int paintOffsetX() {
    return 0;
  }

  public int paintOffsetY() {
    return 0;
  }

  public int marginX() {
    assert this.posX != -1 : "Attempt to get unset X position!";
    return this.posX;
  }

  public int marginY() {
    assert this.posY != -1 : "Attempt to get unset Y position!";
    return this.posY;
  }

  public int borderX() {
    assert this.posX != -1 : "Attempt to get unset X position!";
    return this.posX;
  }

  public int borderY() {
    assert this.posY != -1 : "Attempt to get unset Y position!";
    return this.posY;
  }

  public int contentX() {
    return borderX();
  }

  public int contentY() {
    return borderY();
  }

  public int marginWidth() {
    return this.width;
  }

  public int marginHeight() {
    return this.height;
  }

  public int borderWidth() {
    return this.width;
  }

  public int borderHeight() {
    return this.height;
  }

  public int contentWidth() {
    return this.width;
  }

  public int contentHeight() {
    return this.height;
  }

}
