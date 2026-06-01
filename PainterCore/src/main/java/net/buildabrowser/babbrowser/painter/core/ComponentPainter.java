package net.buildabrowser.babbrowser.painter.core;

public interface ComponentPainter<T> extends Painter {
  
  T createComponent(CanvasCallbacks callbacks);

}
