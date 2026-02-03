package net.buildabrowser.babbrowser.browser.render.layout.imp;

import net.buildabrowser.babbrowser.browser.render.layout.PositionTracker;

public class PositionTrackerImp implements PositionTracker {
  
  private float posX = 0;
  private float posY = 0;

  @Override
  public void adjustPos(float x, float y) {
    this.posX += x;
    this.posY += y;
  }

  @Override
  public long mark() {
    return (Float.floatToIntBits(this.posX) << 32) | (Float.floatToIntBits(this.posY) & 0xFFFFFFFFL);
  }

  @Override
  public void restoreMark(long mark) {
    this.posX = Float.intBitsToFloat((int)(mark >> 32));
    this.posY = Float.intBitsToFloat((int)(mark & 0xFFFFFFFFL));
  }

  @Override
  public float posX() {
    return this.posX;
  }

  @Override
  public float posY() {
    return this.posY;
  }

  @Override
  public void reset() {
    this.posX = 0;
    this.posY = 0;
  }

}