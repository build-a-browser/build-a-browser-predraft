package net.buildabrowser.babbrowser.stream.imp;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.buildabrowser.babbrowser.stream.ReadRequest;
import net.buildabrowser.babbrowser.stream.ReadableStream;
import net.buildabrowser.babbrowser.stream.ReadableStreamController;
import net.buildabrowser.babbrowser.stream.ReadableStreamGenericReader;
import net.buildabrowser.babbrowser.stream.UnderlyingSource;
import net.buildabrowser.babbrowser.stream.UnderlyingSource.ReadableStreamType;
import net.buildabrowser.babbrowser.stream.UnderlyingSource.UnderlyingSourceCancelCallback;
import net.buildabrowser.babbrowser.stream.UnderlyingSource.UnderlyingSourcePullCallback;
import net.buildabrowser.babbrowser.stream.UnderlyingSource.UnderlyingSourceStartCallback;

public class ReadableStreamImp implements ReadableStream {

  ReadableStreamController controller;
  @SuppressWarnings("unused")
  private boolean detached;
  boolean disturbed;
  ReadableStreamGenericReaderImp reader;
  ReadableStreamState state = ReadableStreamState.READABLE;
  Object storedError;

  public ReadableStreamImp(UnderlyingSource underlyingSource) {
    // TODO: More stuff
    if (
      underlyingSource != null
      && underlyingSource.type.equals(ReadableStreamType.BYTES)
    ) {
      // TODO: Backpressure stuff
      setupReadableByteStreamControllerFromUnderlyingSource(underlyingSource);
    }
  }

  @Override
  public ReadableStreamGenericReader getReader(ReadableStreamGetReaderOptions options) {
    if (options == null || options.mode == null) {
      return new ReadableStreamDefaultReaderImp(this);
    }

    assert options.mode == ReadableStreamReaderMode.BYOB;
    // TODO: Return BYOB reader
    throw new UnsupportedOperationException("Not implemented!");
  }

  public void enqueue(Object chunk) {
    // TODO: A proper implementation of this
    ((ReadableByteStreamControllerImp) controller).enqueue((ByteBuffer) chunk);
  }

  public void requestClose() {
    // TODO: Actually handle this properly
    if (controller instanceof ReadableByteStreamControllerImp controllerImp) {
      controllerImp.close();
    }
  }

  public boolean isLocked() {
    return reader != null;
  }

  void addReadRequest(ReadRequest readRequest) {
    assert reader instanceof ReadableStreamDefaultReaderImp;
    assert this.state.equals(ReadableStreamState.READABLE);
    ((ReadableStreamDefaultReaderImp) reader).readRequests.add(readRequest);
  }

  int getNumReadRequests() {
    assert hasDefaultReader();
    return ((ReadableStreamDefaultReaderImp) reader).readRequests.size();
  }

  void close() {
    assert state.equals(ReadableStreamState.READABLE);
    this.state = ReadableStreamState.CLOSED;
    if (reader == null) return;
    // TODO: Resolve closedPromise
    if (reader instanceof ReadableStreamDefaultReaderImp defaultReader) {
      List<ReadRequest> readRequests = List.copyOf(defaultReader.readRequests);
      defaultReader.readRequests.clear();
      for (ReadRequest readRequest: readRequests) {
        readRequest.close();
      }
    }
  }

  void fulfillReadRequest(ByteBuffer transferredView, boolean done) {
    assert hasDefaultReader();
    ReadableStreamDefaultReaderImp defaultReader = (ReadableStreamDefaultReaderImp) reader;
    List<ReadRequest> readRequests = defaultReader.readRequests;
    assert !readRequests.isEmpty();
    ReadRequest readRequest = readRequests.removeFirst();
    if (done) {
      readRequest.close();
    } else {
      readRequest.chunk(transferredView);
    }
  }

  boolean hasDefaultReader() {
    return reader instanceof ReadableStreamDefaultReaderImp;
  }

  private void setupReadableByteStreamControllerFromUnderlyingSource(UnderlyingSource underlyingSource) {
    UnderlyingSourceStartCallback startAlgorithm = underlyingSource == null || underlyingSource.start == null ?
      _ -> null :
      underlyingSource.start;
    UnderlyingSourcePullCallback pullAlgorithm = underlyingSource == null || underlyingSource.pull == null ?
      _ -> CompletableFuture.completedFuture(null) :
      underlyingSource.pull;
    UnderlyingSourceCancelCallback cancelAlgorithm = underlyingSource == null || underlyingSource.cancel == null ?
      _ -> CompletableFuture.completedFuture(null) :
      underlyingSource.cancel;
    // TODO: Handle autoAllocateChunkSize
    setupReadableByteStreamController(startAlgorithm, pullAlgorithm, cancelAlgorithm);
  }

  // TODO: It would be nice if Java let me set a final from within a method that is not the constructor...
  private void setupReadableByteStreamController(
    UnderlyingSourceStartCallback startAlgorithm,
    UnderlyingSourcePullCallback pullAlgorithm,
    UnderlyingSourceCancelCallback cancelAlgorithm
  ) {
    assert this.controller == null;
    ReadableByteStreamControllerImp controller = new ReadableByteStreamControllerImp(
      this, pullAlgorithm, cancelAlgorithm);
    this.controller = controller;
    Object startResult = startAlgorithm.apply(controller);
    // TODO: The spec uses a promise, but I think just a try/catch has the same result?
    CompletableFuture<?> startPromise = startResult instanceof CompletableFuture completeableFuture ?
      completeableFuture :
      CompletableFuture.completedFuture(null);
    startPromise.thenAccept(_ -> controller.postStart());
    startPromise.exceptionally(e -> { controller.error(e); return null; });
  }

  public static enum ReadableStreamState {
    CLOSED, ERRORED, READABLE;
  }
  
}
