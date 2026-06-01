package net.buildabrowser.babbrowser.painter.core;

public interface Paint {
  
  void setColor(int color);

  int getColor();

  void setFont(LoadedFont font);
  
  LoadedFont getFont();

}
