package net.buildabrowser.babbrowser.painter.core;

public interface CanvasCallbacks {
  
  void layout(float width, float height);

  void paint(PaintCanvas canvas);
  
}
