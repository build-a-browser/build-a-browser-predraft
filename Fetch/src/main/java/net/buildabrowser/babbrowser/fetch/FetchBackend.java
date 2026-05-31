package net.buildabrowser.babbrowser.fetch;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchResponse;

public interface FetchBackend {
  
  // TODO: Also require a connection
  void makeRequest(
    MutableFetchResponse response, FetchRequest request,
    Consumer<Optional<ByteBuffer>> byteConsumer
  );

  // TODO: Better abstraction for this
  FetchResponse fetchFile(FetchRequest request);

}
