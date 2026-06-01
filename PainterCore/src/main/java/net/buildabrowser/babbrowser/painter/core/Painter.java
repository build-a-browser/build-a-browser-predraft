package net.buildabrowser.babbrowser.painter.core;

public interface Painter {
  
  ResourceLoader resourceLoader();

  PaintBitMap createPaintBitMap(int width, int height);

}
