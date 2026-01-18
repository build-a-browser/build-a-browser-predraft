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

  public int posX() {
    assert this.posX != -1 : "Attempt to get unset X position!";
    return this.posX;
  }

  public int posY() {
    assert this.posX != -1 : "Attempt to get unset Y position!";
    return this.posY;
  }

  public int width() {
    return this.width;
  }

  public int height() {
    return this.height;
  }

}
