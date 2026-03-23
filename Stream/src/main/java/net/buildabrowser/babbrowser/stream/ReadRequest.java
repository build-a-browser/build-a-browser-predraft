package net.buildabrowser.babbrowser.stream;

import java.nio.ByteBuffer;

public interface ReadRequest {
  
  void chunk(ByteBuffer chunk);

  void close();

  void error(Object e);

}
