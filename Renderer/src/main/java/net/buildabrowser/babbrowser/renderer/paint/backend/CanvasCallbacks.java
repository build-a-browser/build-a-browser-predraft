package net.buildabrowser.babbrowser.renderer.paint.backend;

public interface CanvasCallbacks {
  
  void layout();

  void paint(PaintCanvas canvas);
  
}
