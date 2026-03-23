package net.buildabrowser.babbrowser.stream;

import java.nio.ByteBuffer;

public interface ReadableByteStreamController extends ReadableStreamController {
  
  ReadableStreamBYOBRequest byobRequest();

  Double desiredSize();

  void close();

  // TODO: Should be an ArrayBufferView
  void enqueue(ByteBuffer chunk);

  void error(Object e);

}
