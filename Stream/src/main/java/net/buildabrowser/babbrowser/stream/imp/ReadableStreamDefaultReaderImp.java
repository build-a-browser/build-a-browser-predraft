package net.buildabrowser.babbrowser.stream.imp;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.stream.ReadRequest;
import net.buildabrowser.babbrowser.stream.ReadableStreamDefaultReader;
import net.buildabrowser.babbrowser.stream.imp.ReadableStreamImp.ReadableStreamState;

public class ReadableStreamDefaultReaderImp
  extends ReadableStreamGenericReaderImp implements ReadableStreamDefaultReader {

  final List<ReadRequest> readRequests = new LinkedList<>();

  public ReadableStreamDefaultReaderImp(ReadableStreamImp stream) {
    super(stream);
    stream.reader = this;
  }

  @Override
  public void readAllBytes(Consumer<byte[]> successSteps, Consumer<Object> failureSteps) {
    read(new ReadAllBytesReadRequest(this, successSteps, failureSteps));
  }

  @Override
  public void read(ReadRequest request) {
    assert stream != null;
    stream.disturbed = true;
    if (stream.state.equals(ReadableStreamState.CLOSED)) {
      request.close();
    } else if (stream.state.equals(ReadableStreamState.ERRORED)) {
      request.error(stream.storedError);
    } else {
      assert stream.state.equals(ReadableStreamState.READABLE);
      stream.controller.pull(request);
    }
  }
  
}
