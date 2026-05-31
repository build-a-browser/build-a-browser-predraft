package net.buildabrowser.babbrowser.renderer.paint.backend;

import java.util.function.Consumer;

public interface PaintBitMap {

  void withCanvas(Consumer<PaintCanvas> paintFunc);

}
