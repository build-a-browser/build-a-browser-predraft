package net.buildabrowser.babbrowser.stream;

import java.nio.ByteBuffer;

public interface ReadableStreamBYOBRequest {

  // TODO: Use an ArrayBufferView
  ByteBuffer view();

  void respond(long bytesWritten);

  void respondWithNewView(ByteBuffer view);

}
