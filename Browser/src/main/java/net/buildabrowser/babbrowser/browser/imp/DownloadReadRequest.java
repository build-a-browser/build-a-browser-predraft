package net.buildabrowser.babbrowser.browser.imp;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.stream.ReadRequest;
import net.buildabrowser.babbrowser.stream.ReadableStreamDefaultReader;

public class DownloadReadRequest implements ReadRequest {

  private final ReadableStreamDefaultReader streamReader;
  private final Consumer<ByteBuffer> chunkSteps;
  private final Runnable successSteps;
  private final Consumer<Object> failureSteps;

  public DownloadReadRequest(
    ReadableStreamDefaultReader streamReader,
    Consumer<ByteBuffer> chunkSteps,
    Runnable successSteps,
    Consumer<Object> failureSteps
  ) {
    this.streamReader = streamReader;
    this.chunkSteps = chunkSteps;
    this.successSteps = successSteps;
    this.failureSteps = failureSteps;
  }

  @Override
  public void chunk(ByteBuffer chunk) {
    chunkSteps.accept(chunk);
    streamReader.read(this);
  }

  @Override
  public void close() {
    successSteps.run();
  }

  @Override
  public void error(Object e) {
    failureSteps.accept(e);
  }

}