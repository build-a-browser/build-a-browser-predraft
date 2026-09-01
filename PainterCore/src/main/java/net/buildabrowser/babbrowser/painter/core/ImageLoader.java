package net.buildabrowser.babbrowser.painter.core;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface ImageLoader {

  void onChunk(ByteBuffer chunk) throws IOException;

  void onDone() throws IOException;

  void onFailure(Exception e);

  LoadedImage currentImage();

}
