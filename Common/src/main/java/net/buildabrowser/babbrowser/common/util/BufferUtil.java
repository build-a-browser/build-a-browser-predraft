package net.buildabrowser.babbrowser.common.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public final class BufferUtil {
  
  private BufferUtil() {}

  public static void writeBufferToStream(
    ByteBuffer buffer,
    OutputStream stream
  ) throws IOException {
    if (buffer.hasArray()) {
      stream.write(
        buffer.array(),
        buffer.arrayOffset() + ((Buffer) buffer).position(),
        buffer.remaining());
      ((Buffer) buffer).position(buffer.limit());
    } else {
      byte[] bytes = new byte[buffer.remaining()];
      buffer.get(bytes);
      stream.write(bytes);
    }
  }

}
