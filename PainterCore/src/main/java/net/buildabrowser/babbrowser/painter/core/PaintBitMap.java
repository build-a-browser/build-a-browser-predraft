package net.buildabrowser.babbrowser.painter.core;

import java.util.function.Consumer;

public interface PaintBitMap {

  void withCanvas(Consumer<PaintCanvas> paintFunc);

}
