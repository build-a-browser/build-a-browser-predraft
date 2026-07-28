package net.buildabrowser.babbrowser.painter.core;

public interface Paint {
  
  void setColor(int color);

  int getColor();

  void setFont(LoadedFont font);
  
  LoadedFont getFont();

  void setFilled(boolean filled);

  boolean getFilled();

  void setStrokeSize(float strokeSize);

  float getStrokeSize();

}
