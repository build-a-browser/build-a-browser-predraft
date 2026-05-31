package net.buildabrowser.babbrowser.renderer.paint.backend;

public interface CanvasCallbacks {
  
  void layout(float width, float height);

  void paint(PaintCanvas canvas);
  
}
