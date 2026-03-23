package net.buildabrowser.babbrowser.stream.imp;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.stream.ReadRequest;

public class ReadAllBytesReadRequest implements ReadRequest {

  private final ReadableStreamDefaultReaderImp streamReader;
  private final Consumer<byte[]> successSteps;
  private final Consumer<Object> failureSteps;

  private final List<ByteBuffer> allByteBuffers = new LinkedList<>();

  private int numBytes = 0;

  public ReadAllBytesReadRequest(
    ReadableStreamDefaultReaderImp streamReader,
    Consumer<byte[]> successSteps, Consumer<Object> failureSteps
  ) {
    this.streamReader = streamReader;
    this.successSteps = successSteps;
    this.failureSteps = failureSteps;
  }

  @Override
  public void chunk(ByteBuffer chunk) {
    numBytes += chunk.limit();
    allByteBuffers.add(chunk);
    streamReader.read(this);
  }

  @Override
  public void close() {
    byte[] bytes = new byte[numBytes];
    int offset = 0;
    for (ByteBuffer buffer: allByteBuffers) {
      int length = buffer.limit();
      buffer.get(bytes, offset, length);
      offset += length;
    }
    successSteps.accept(bytes);
  }

  @Override
  public void error(Object e) {
    failureSteps.accept(e);
  }

}
