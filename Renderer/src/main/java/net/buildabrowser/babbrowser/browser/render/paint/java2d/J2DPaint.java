package net.buildabrowser.babbrowser.browser.render.paint.java2d;

import net.buildabrowser.babbrowser.browser.render.paint.Paint;

public class J2DPaint implements Paint {

  private int color;
  private float offsetX;
  private float offsetY;

  @Override
  public void setColor(int color) {
    this.color = color;
  }

  @Override
  public int getColor() {
    return this.color;
  }

  @Override
  public void incOffset(float x, float y) {
    this.offsetX += x;
    this.offsetY += y;
  }

  @Override
  public void setOffset(float x, float y) {
    this.offsetX = x;
    this.offsetY = y;
  }

  @Override
  public float offsetX() {
    return this.offsetX;
  }

  @Override
  public float offsetY() {
    return this.offsetY;
  }

}
