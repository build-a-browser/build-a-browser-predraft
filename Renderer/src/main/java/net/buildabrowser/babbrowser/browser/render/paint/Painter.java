package net.buildabrowser.babbrowser.browser.render.paint;

import java.awt.Graphics;
import java.util.function.Consumer;

public interface Painter {
  
  ResourceLoader resourceLoader();

  void withCanvas(Graphics g, int width, int height, Consumer<PaintCanvas> paintFunc);

}
