package net.buildabrowser.babbrowser.render.paint.backend;

import java.util.function.Consumer;

public interface PaintBitMap {

  void withCanvas(Consumer<PaintCanvas> paintFunc);

}
