package net.buildabrowser.babbrowser.browser.render.paint;

public interface Paint {
  
  void setColor(int color);

  int getColor();

  void incOffset(float x, float y);

  void setOffset(float x, float y);

  float offsetX();

  float offsetY();

}
