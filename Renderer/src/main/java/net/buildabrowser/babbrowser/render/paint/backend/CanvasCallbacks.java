package net.buildabrowser.babbrowser.render.paint.backend;

public interface CanvasCallbacks {
  
  void layout();

  void paint(PaintCanvas canvas);
  
}
