package net.buildabrowser.babbrowser.render.paint;

import java.awt.Component;

public interface Painter {
  
  ResourceLoader resourceLoader();

  PaintBitMap createPaintBitMap(int width, int height);

  Component createComponent(CanvasCallbacks callbacks);

}
