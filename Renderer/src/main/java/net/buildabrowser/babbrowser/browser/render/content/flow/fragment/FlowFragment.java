package net.buildabrowser.babbrowser.browser.render.content.flow.fragment;

public abstract class FlowFragment {
  
  private final int width;
  private final int height;

  private int posX;
  private int posY;

  public FlowFragment(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void setPos(int x, int y) {
    this.posX = x;
    this.posY = y;
  }

  public int posX() {
    return this.posX;
  }

  public int posY() {
    return this.posY;
  }

  public int width() {
    return this.width;
  }

  public int height() {
    return this.height;
  }

}
