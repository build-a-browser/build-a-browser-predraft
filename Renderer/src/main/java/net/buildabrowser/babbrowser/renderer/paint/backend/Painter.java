package net.buildabrowser.babbrowser.renderer.paint.backend;

public interface Painter {
  
  ResourceLoader resourceLoader();

  PaintBitMap createPaintBitMap(int width, int height);

}
