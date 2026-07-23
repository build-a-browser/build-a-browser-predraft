package net.buildabrowser.babbrowser.fetch.imp;

import java.nio.ByteBuffer;

import net.buildabrowser.babbrowser.fetch.FetchBody;
import net.buildabrowser.babbrowser.fetch.FetchBody.FetchBodyWithType;
import net.buildabrowser.babbrowser.stream.ReadableStream;
import net.buildabrowser.babbrowser.stream.UnderlyingSource;
import net.buildabrowser.babbrowser.stream.UnderlyingSource.ReadableStreamType;

public final class FetchImpUtil {
  
  private FetchImpUtil() {}

  public static FetchBody getBytesAsABody(byte[] bytes) {
    return safelyExtractABodyWithType(bytes).body();
  }

  public static FetchBodyWithType safelyExtractABodyWithType(Object object) {
    // TODO: Assertion
    return extractABodyWithType(object, false);
  }

  public static FetchBodyWithType extractABodyWithType(Object object, boolean keepalive) {
    ReadableStream stream = null;
    if (object instanceof ReadableStream readableStream) {
      stream = readableStream;
    } else {
      // TODO: Blob
      // TODO: Properly implement "set up with byte reading support"
      UnderlyingSource underlyingSource = new UnderlyingSource();
      underlyingSource.type = ReadableStreamType.BYTES;
      stream = ReadableStream.create(underlyingSource);
    }

    assert stream instanceof ReadableStream;

    Object source = null;
    Integer length = null;
    String type = null;
    // TODO: Support other input types
    switch (object) {
      case byte[] bytes -> {
        source = bytes;
        length = bytes.length;
        // Just do this here to avoid an extra if.
        // The spec is worded a bit confusingly, and since
        // byte[] is fixed I dunno why it needs to be in parallel
        if (bytes.length > 0) {
          ReadableStream.enqueue(stream, ByteBuffer.wrap(bytes));
        }
        ReadableStream.close(stream);
      }
      case ByteBuffer byteBuffer -> {
        source = byteBuffer;
        length = byteBuffer.limit();
        if (byteBuffer.limit() > 0) {
          ReadableStream.enqueue(stream, byteBuffer);
        }
        ReadableStream.close(stream);
      }
      default -> {}
    }

    FetchBody body = new FetchBody(stream, source, length);
    return new FetchBodyWithType(body, type);
  }

}
