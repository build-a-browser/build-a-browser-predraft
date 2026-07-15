package net.buildabrowser.babbrowser.renderer.event;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public sealed interface EventHandlerResponse 
  permits EventHandlerResponse.SyncEventHandlerResponse, EventHandlerResponse.AsyncEventHandlerResponse {

  public static final SyncEventHandlerResponse UNHANDLED = Holder.UNHANDLED;
  public static final SyncEventHandlerResponse HANDLED = Holder.HANDLED;
  public static final SyncEventHandlerResponse PERFORM_DEFAULT = Holder.PERFORM_DEFAULT;

  static class Holder {
    private static final SyncEventHandlerResponse UNHANDLED = SyncEventHandlerResponse.UNHANDLED;
    private static final SyncEventHandlerResponse HANDLED = SyncEventHandlerResponse.HANDLED;
    private static final SyncEventHandlerResponse PERFORM_DEFAULT = SyncEventHandlerResponse.PERFORM_DEFAULT;
  }

  default boolean isUnhandled() {
    return this.equals(SyncEventHandlerResponse.UNHANDLED);
  }

  EventHandlerResponse then(
    Function<SyncEventHandlerResponse, EventHandlerResponse> onResponse
  );

  default EventHandlerResponse thenDefault(
    Function<SyncEventHandlerResponse, EventHandlerResponse> onResponse
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
      Function<SyncEventHandlerResponse, EventHandlerResponse> onResponse
    ) {
      return onResponse.apply(this);
    }
  }

  static record AsyncEventHandlerResponse(
    CompletableFuture<SyncEventHandlerResponse> future
  ) implements EventHandlerResponse {

    @Override
    public EventHandlerResponse then(
      Function<SyncEventHandlerResponse, EventHandlerResponse> onResponse
    ) {
      CompletableFuture<SyncEventHandlerResponse> newFuture = future.thenCompose(prevResponse -> {
        EventHandlerResponse newResponse = onResponse.apply(prevResponse);
        if (newResponse instanceof AsyncEventHandlerResponse asyncEventHandlerResponse) {
          return asyncEventHandlerResponse.future();
        } else {
          return CompletableFuture.completedFuture((SyncEventHandlerResponse) newResponse);
        }
      });
      return new AsyncEventHandlerResponse(newFuture);
    }
    
  }

}
