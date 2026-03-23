package net.buildabrowser.babbrowser.fetch;

import java.util.Optional;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.mutable.MutableFetchResponse;

public interface FetchBackend {
  
  // TODO: Also require a connection
  void makeRequest(
    MutableFetchResponse response, FetchRequest request,
    Consumer<Optional<byte[]>> byteConsumer
  );

}
