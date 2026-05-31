package net.buildabrowser.babbrowser.renderer.content.scroll;

public class ScrollBarState {

  private boolean hovered = false;
  private float winStart = Float.NaN;
  private float scrollStart = Float.NaN;

  public void setHovered(boolean hovered) {
    this.hovered = hovered;
  }

  public boolean hovered() {
    return this.hovered;
  }

  public boolean active() {
    return !Float.isNaN(winStart);
  }

  public void activate(float startWin, float startScroll) {
    this.winStart = startWin;
    this.scrollStart = startScroll;
  }

  public void deactivate() {
    this.winStart = Float.NaN;
    this.scrollStart = Float.NaN;
  }

  public float winStart() {
    return this.winStart;
  }

  public float scrollStart() {
    return this.scrollStart;
  }
  
}
