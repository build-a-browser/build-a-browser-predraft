package net.buildabrowser.babbrowser.renderer.composite;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.renderer.paint.backend.PaintCanvas;

public interface LayerBitMap {
  
  void update(Consumer<PaintCanvas> paintFunc);

  void draw(int x, int y, PaintCanvas canvas);

  void resize(int x, int y, int width, int height);

}
