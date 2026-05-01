package net.buildabrowser.babbrowser.stream;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public final class UnderlyingSource {
  
  public UnderlyingSourceStartCallback start;

  public UnderlyingSourcePullCallback pull;

  public UnderlyingSourceCancelCallback cancel;

  public ReadableStreamType type;

  public Long autoAllocateChunkSize;

  public interface UnderlyingSourceStartCallback
    extends Function<ReadableStreamController, Object> {}
  public interface UnderlyingSourcePullCallback
    extends Function<ReadableByteStreamController, CompletableFuture<Void>> {}
  public interface UnderlyingSourceCancelCallback
    extends Function<Object, CompletableFuture<Void>> {}

  public enum ReadableStreamType {
    BYTES;
  }

}
