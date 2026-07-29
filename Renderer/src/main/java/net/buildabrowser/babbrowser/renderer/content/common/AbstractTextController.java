package net.buildabrowser.babbrowser.renderer.content.common;

public abstract class AbstractTextController implements TextController {

  private int cursorX = 0;
  private float scrollX = 0;
  private int cursorY = 0;
  private float scrollY = 0;
  private boolean isReplaceMode = false;

  @Override
  public int cursorX() {
    return this.cursorX;
  }

  @Override
  public void setCursorX(int cursorX) {
    this.cursorX = cursorX;
  }

  @Override
  public int cursorY() {
    return this.cursorY;
  }

  @Override
  public void setCursorY(int cursorY) {
    this.cursorY = cursorY;
  }

  @Override
  public float scrollX() {
    return this.scrollX;
  }

  @Override
  public void setScrollX(float scrollX) {
    this.scrollX = scrollX;
  }

  @Override
  public float scrollY() {
    return this.scrollY;
  }

  @Override
  public void setScrollY(float scrollY) {
    this.scrollY = scrollY;
  }

  @Override
  public boolean isReplaceMode() {
    return this.isReplaceMode;
  }

  @Override
  public void setIsReplaceMode(boolean isReplaceMode) {
    this.isReplaceMode = isReplaceMode;
  }
  
}
