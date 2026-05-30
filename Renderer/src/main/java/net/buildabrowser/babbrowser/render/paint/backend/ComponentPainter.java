package net.buildabrowser.babbrowser.render.paint.backend;

public interface ComponentPainter<T> extends Painter {
  
  T createComponent(CanvasCallbacks callbacks);

}
