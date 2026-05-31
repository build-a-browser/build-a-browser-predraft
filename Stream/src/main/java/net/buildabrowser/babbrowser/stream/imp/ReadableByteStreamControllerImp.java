package net.buildabrowser.babbrowser.stream.imp;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import net.buildabrowser.babbrowser.stream.ReadRequest;
import net.buildabrowser.babbrowser.stream.ReadableByteStreamController;
import net.buildabrowser.babbrowser.stream.ReadableStreamBYOBRequest;
import net.buildabrowser.babbrowser.stream.UnderlyingSource.UnderlyingSourceCancelCallback;
import net.buildabrowser.babbrowser.stream.UnderlyingSource.UnderlyingSourcePullCallback;
import net.buildabrowser.babbrowser.stream.imp.ReadableStreamImp.ReadableStreamState;

public class ReadableByteStreamControllerImp implements ReadableByteStreamController {

  private final List<PullIntoDescriptor> pendingPullIntos = new LinkedList<>();
  private final List<ByteBuffer> queue = new LinkedList<>(); // TODO: Use the proper entry type

  private final ReadableStreamImp stream;

  @SuppressWarnings("unused")
  private UnderlyingSourceCancelCallback cancelAlgorithm;
  @SuppressWarnings("unused")
  private UnderlyingSourcePullCallback pullAlgorithm;
  
  private boolean closeRequested;
  private boolean pullAgain;
  private boolean pulling;
  private int queueTotalSize;
  @SuppressWarnings("unused")
  private boolean started;

  public ReadableByteStreamControllerImp(
    ReadableStreamImp stream,
    UnderlyingSourcePullCallback pullAlgorithm,
    UnderlyingSourceCancelCallback cancelAlgorithm
  ) {
    this.stream = stream;
    this.pullAlgorithm = pullAlgorithm;
    this.cancelAlgorithm = cancelAlgorithm;
  }

  @Override
  public ReadableStreamBYOBRequest byobRequest() {
    // TODO
    return null;
  }

  @Override
  public Double desiredSize() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'desiredSize'");
  }

  @Override
  public void close() {
    if (
      closeRequested
      || !stream.state.equals(ReadableStreamState.READABLE)
    ) return;
    
    if (queueTotalSize > 0) {
      this.closeRequested = true;
      return;
    }

    // TODO: Handle pullIntos

    clearAlgorithms();
    stream.close();
  }

  @Override
  public void enqueue(ByteBuffer chunk) {
    if (chunk.limit() == 0) {
      // TODO: Type error
      throw new IllegalArgumentException("Chunk may not be empty!");
    }
    // TODO: Some other steps

    innerEnqueue(chunk);
  }

  @Override
  public void error(Object e) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'error'");
  }

  // "Internal methods"

  @Override
  public void cancel(Object reason) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'cancel'");
  }

  @Override
  public void pull(ReadRequest readRequest) {
    assert stream.hasDefaultReader();
    if (this.queueTotalSize > 0) {
      assert stream.getNumReadRequests() == 0;
      fillReadRequestFromQueue(readRequest);
      return;
    }
    // TODO: Auto-allocate chunk
    stream.addReadRequest(readRequest);
    callPullIfNeeded();
  }

  //

  void postStart() {
    this.started = true;
    assert !this.pulling;
    assert !this.pullAgain;
    callPullIfNeeded();
  }

  private void innerEnqueue(ByteBuffer chunk) {
    if (
      closeRequested
      || !stream.state.equals(ReadableStreamState.READABLE)
    ) return;
    // TODO: Check detached, transfer the buffer
    ByteBuffer transferredBuffer = chunk;
    // TODO: Check pending pull intos
    
    if (stream.reader instanceof ReadableStreamDefaultReaderImp reader) {
      innerEnqueDefaultReader(transferredBuffer, reader);
    } else { // TODO: Check if stream has BYOB reader
      assert !stream.isLocked();
      enqueueChunkToQueue(transferredBuffer);
    }
    callPullIfNeeded();
  }

  private void innerEnqueDefaultReader(ByteBuffer transferredBuffer, ReadableStreamDefaultReaderImp reader) {
    processReadRequestsUsingQueue(reader);
    if (stream.getNumReadRequests() == 0) {
      assert pendingPullIntos.isEmpty();
      enqueueChunkToQueue(transferredBuffer);
    } else {
      assert queue.isEmpty();
      if (!pendingPullIntos.isEmpty()) {
        // TODO: Fulfill pull into
      }
      // TODO: Create a transferred view
      ByteBuffer transferredView = transferredBuffer;
      stream.fulfillReadRequest(transferredView, false);
    }
  }

  private void processReadRequestsUsingQueue(ReadableStreamDefaultReaderImp reader) {
    List<ReadRequest> readRequests = reader.readRequests;
    while (!readRequests.isEmpty()) {
      if (queueTotalSize == 0) return;
      ReadRequest readRequest = readRequests.remove(0);
      fillReadRequestFromQueue(readRequest);
    }
  }

  private void fillReadRequestFromQueue(ReadRequest readRequest) {
    assert queueTotalSize > 0;
    ByteBuffer entry = queue.remove(0);
    this.queueTotalSize -= entry.limit();
    handleQueueDrain();
    // TODO: Create a view
    readRequest.chunk(entry);
  }

  private void handleQueueDrain() {
    assert stream.state.equals(ReadableStreamState.READABLE);
    if (queueTotalSize == 0 && closeRequested) {
      clearAlgorithms();
      stream.close();
    } else {
      callPullIfNeeded();
    }
  }

  // TODO: Also take offset/length
  private void enqueueChunkToQueue(ByteBuffer buffer) {
    queue.add(buffer);
    this.queueTotalSize += buffer.limit();
  }

  private void callPullIfNeeded() {
    // TODO: Implement
  }

  private void clearAlgorithms() {
    this.pullAlgorithm = null;
    this.cancelAlgorithm = null;
  }

}
