package net.buildabrowser.babbrowser.browser.net.imp;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.removeLast;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchResponse;

public class FetchHostPool {

  private final List<QueuedRequest> requests = new LinkedList<>();
  private final int maxRequests;

  private int currentRequests;

  public FetchHostPool(int maxRequests) {
    this.maxRequests = maxRequests;
  }

  public static record QueuedRequest(
    MutableFetchResponse response,
    FetchRequest request,
    Consumer<Optional<ByteBuffer>> byteConsumer
  ) {}

  public QueuedRequest unqueueRequest() {
    if (requests.isEmpty()) return null;
    return removeLast(requests);
  }

  public void queueRequest(QueuedRequest queuedRequest) {
    requests.add(queuedRequest);
  }

  public void releaseStream() {
    currentRequests--;
  }

  public boolean acquireStream() {
    if (currentRequests == maxRequests) {
      return false;
    }

    currentRequests++;
    return true;
  }

  public boolean hasNoData() {
    return
      currentRequests == maxRequests
      && requests.isEmpty();
  }

}
