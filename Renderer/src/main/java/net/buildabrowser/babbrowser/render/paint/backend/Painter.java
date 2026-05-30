package net.buildabrowser.babbrowser.render.paint.backend;

public interface Painter {
  
  ResourceLoader resourceLoader();

  PaintBitMap createPaintBitMap(int width, int height);

}
