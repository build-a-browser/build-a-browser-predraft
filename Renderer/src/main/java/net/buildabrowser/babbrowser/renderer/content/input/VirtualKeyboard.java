package net.buildabrowser.babbrowser.renderer.content.input;

import net.buildabrowser.babbrowser.renderer.input.OffThreadWriteTextController;

public interface VirtualKeyboard {

  default void onInputConnected(
    OffThreadWriteTextController textController
  ) {}
  
  default void show() {}

  default void close() {}

}
