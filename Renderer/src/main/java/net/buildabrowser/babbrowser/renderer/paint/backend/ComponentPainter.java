package net.buildabrowser.babbrowser.renderer.paint.backend;

public interface ComponentPainter<T> extends Painter {
  
  T createComponent(CanvasCallbacks callbacks);

}
