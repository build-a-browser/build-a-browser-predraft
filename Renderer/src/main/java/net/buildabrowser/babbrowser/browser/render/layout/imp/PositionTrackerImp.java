package net.buildabrowser.babbrowser.browser.render.layout.imp;

import net.buildabrowser.babbrowser.browser.render.layout.PositionTracker;

public class PositionTrackerImp implements PositionTracker {
  
  private int posX = 0;
  private int posY = 0;

  @Override
  public void adjustPos(int x, int y) {
    this.posX = x;
    this.posY = y;
  }

  @Override
  public long mark() {
    return (this.posX << 32) | this.posY;
  }

  @Override
  public void restoreMark(long mark) {
    this.posX = (int)(mark >> 32);
    this.posY = (int)(mark & 0xFFFFFFFFL);
  }

  @Override
  public int posX() {
    return this.posX;
  }

  @Override
  public int posY() {
    return this.posY;
  }

  @Override
  public void reset() {
    this.posX = 0;
    this.posY = 0;
  }

}