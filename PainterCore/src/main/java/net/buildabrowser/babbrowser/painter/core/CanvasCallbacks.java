package net.buildabrowser.babbrowser.painter.core;

public interface CanvasCallbacks {
  
  default void layout(float width, float height) {}

  default void paint(PaintCanvas canvas) {}
  
}
