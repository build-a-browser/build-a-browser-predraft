package net.buildabrowser.babbrowser.render.paint;

import java.awt.Graphics;
import java.util.function.Consumer;

public interface Painter {
  
  ResourceLoader resourceLoader();

  PaintBitMap createPaintBitMap(int width, int height);

  void withCanvas(Graphics g, int width, int height, Consumer<PaintCanvas> paintFunc);

}
