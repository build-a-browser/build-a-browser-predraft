package net.buildabrowser.babbrowser.renderer.event;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public sealed interface EventHandlerResponse 
  permits EventHandlerResponse.SyncEventHandlerResponse, EventHandlerResponse.AsyncEventHandlerResponse {

  public static final SyncEventHandlerResponse UNHANDLED = SyncEventHandlerResponse.UNHANDLED;
  public static final SyncEventHandlerResponse HANDLED = SyncEventHandlerResponse.HANDLED;
  public static final SyncEventHandlerResponse PERFORM_DEFAULT = SyncEventHandlerResponse.PERFORM_DEFAULT;

  default boolean isUnhandled() {
    return this.equals(SyncEventHandlerResponse.UNHANDLED);
  }

  EventHandlerResponse then(
    Function<SyncEventHandlerResponse, SyncEventHandlerResponse> onResponse
  );

  default EventHandlerResponse thenDefault(
    Function<SyncEventHandlerResponse, SyncEventHandlerResponse> onResponse
  ) {
    return then(syncResponse -> {
      if (
        syncResponse.equals(EventHandlerResponse.HANDLED)
      ) return syncResponse;

      return onResponse.apply(syncResponse);
    });
  }

  static enum SyncEventHandlerResponse implements EventHandlerResponse {
    UNHANDLED, HANDLED, PERFORM_DEFAULT;

    @Override
    public EventHandlerResponse then(
      Function<SyncEventHandlerResponse, SyncEventHandlerResponse> onResponse
    ) {
      return onResponse.apply(this);
    }
  }

  static record AsyncEventHandlerResponse(
    CompletableFuture<SyncEventHandlerResponse> future
  ) implements EventHandlerResponse {

    @Override
    public EventHandlerResponse then(
      Function<SyncEventHandlerResponse, SyncEventHandlerResponse> onResponse
    ) {
      CompletableFuture<SyncEventHandlerResponse> newFuture = future.thenApply(onResponse);
      return new AsyncEventHandlerResponse(newFuture);
    }
    
  }

}
