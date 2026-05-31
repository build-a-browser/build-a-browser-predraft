package net.buildabrowser.babbrowser.renderer.paint.backend;

public interface Paint {
  
  void setColor(int color);

  int getColor();

  void incOffset(float x, float y);

  void setOffset(float x, float y);

  float offsetX();

  float offsetY();

  void setFont(LoadedFont font);
  
  LoadedFont getFont();

}
