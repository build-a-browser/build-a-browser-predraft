package net.buildabrowser.babbrowser.fetch.imp;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import net.buildabrowser.babbrowser.fetch.FetchBackend;
import net.buildabrowser.babbrowser.fetch.FetchBody;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.fetch.FetchParameters;
import net.buildabrowser.babbrowser.fetch.FetchParams;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.fetch.FetchUtil;
import net.buildabrowser.babbrowser.fetch.HeaderList;
import net.buildabrowser.babbrowser.fetch.FetchParameters.ProcessResponseConsumeBody;
import net.buildabrowser.babbrowser.mutable.MutableFetchResponse;
import net.buildabrowser.babbrowser.stream.ReadableByteStreamController;
import net.buildabrowser.babbrowser.stream.ReadableStream;
import net.buildabrowser.babbrowser.stream.ReadableStreamBYOBRequest;
import net.buildabrowser.babbrowser.stream.ReadableStreamDefaultReader;
import net.buildabrowser.babbrowser.stream.UnderlyingSource;
import net.buildabrowser.babbrowser.stream.UnderlyingSource.ReadableStreamType;

public class FetchEngineImp implements FetchEngine {

  private final FetchBackend fetchBackend;

  public FetchEngineImp(FetchBackend fetchBackend) {
    this.fetchBackend = fetchBackend;
  }

  @Override
  public void fetch(FetchParameters fetchParameters) {
    // TODO: A ton of random stuff
    FetchParams fetchParams = new FetchParams(
      fetchParameters.request,
      fetchParameters.processResponseConsumeBody);
    mainFetch(fetchParams);
  }

  private void mainFetch(FetchParams fetchParams) {
    FetchResponse response = null;
    // TODO: A ton of random stuff
    if (response == null) {
      response = mainFetchChain(fetchParams);
    }

    fetchResponseHandover(fetchParams, response);
  }

  private void fetchResponseHandover(FetchParams fetchParams, FetchResponse response) {
    // TODO: Other stuff
    ProcessResponseConsumeBody consumeBody = fetchParams.processResponseConsumeBody();
    if (fetchParams.processResponseConsumeBody() != null) {
      if (response.body() == null) {
        // TODO: Use a fetch task
        consumeBody.run(response, true, null);
      } else {
        // TODO: Properly use fullyRead with taskDestination
        ReadableStreamDefaultReader reader = (ReadableStreamDefaultReader)
          response.body().stream().getReader(null);
        reader.readAllBytes(
          bytes -> consumeBody.run(response, true, bytes),
          bytes -> consumeBody.run(response, false, null));
      }
    }
  }

  private FetchResponse mainFetchChain(FetchParams fetchParams) {
    return overrideFetch(OverrideFetchType.HTTP_FETCH, fetchParams);
  }

  private FetchResponse overrideFetch(OverrideFetchType fetchType, FetchParams fetchParams) {
    return overrideFetch(fetchType, fetchParams, false);
  }

  private FetchResponse overrideFetch(
    OverrideFetchType fetchType, FetchParams fetchParams, boolean makeCORSPreflight
  ) {
    return switch (fetchType) {
      case SCHEME_FETCH -> schemeFetch(fetchParams);
      case HTTP_FETCH -> httpFetch(fetchParams, makeCORSPreflight);
    };
  }

  private FetchResponse schemeFetch(FetchParams fetchParams) {
    // TODO: Check if fetchParams cancelled, handle blob
    FetchRequest request = fetchParams.request();
    switch (request.url().getScheme()) {
      case "about":
        if (request.currentURL().getPath().equals("blank")) {
          return FetchResponse.create(
            "OK", HeaderList.create("Content-Type", "text/html;charset=utf-8"),
            FetchUtil.getBytesAsABody(new byte[0]));
        }
        break;
      case "blob":
        // TODO: Implement blob
        break;
      case "data":
        // TODO: Implement data
        break;
      case "file":
        // TODO: Implement file
        break;
      case "http", "https":
        return httpFetch(fetchParams, false);
      default:
        break;
    }

    return FetchResponse.createNetworkError();
  }

  private FetchResponse httpFetch(FetchParams fetchParams, boolean makeCORSPreflight) {
    // TODO: A ton of stuff
    return httpNetworkOrCacheFetch(fetchParams, false, false);
  }

  private FetchResponse httpNetworkOrCacheFetch(
    FetchParams fetchParams, boolean isAuthenticationFetch, boolean isNewConnectionFetch
  ) {
    // TODO: A ton of stuff
    return httpNetworkFetch(fetchParams, isAuthenticationFetch, isNewConnectionFetch);
  }

  private FetchResponse httpNetworkFetch(
    FetchParams fetchParams, boolean isAuthenticationFetch, boolean isNewConnectionFetch
  ) {
    // TODO: A ton of random stuff
    // TODO: Properly re-use connections
    FetchRequest request = fetchParams.request();

    // Doing things a bit out-of-order
    MutableFetchResponse response = FetchResponse.createMutable();

    FutureHolder pullPromise = new FutureHolder();
    UnderlyingSource underlyingSource = new UnderlyingSource();
    underlyingSource.type = ReadableStreamType.BYTES;
    underlyingSource.start = controller -> {
      // TODO: The spec defines the stream as a pull source, but it's easier to implement as a push source for now
      // Come back to this later and correct it.
      fetchBackend.makeRequest(response, request, bytesOpt -> {
        // TODO: Though HttpClient handles content-coding for us, this is where it should be handled.
        ReadableByteStreamController bsController = (ReadableByteStreamController) controller;
        if (bytesOpt.isEmpty()) {
          bsController.close();
          return;
        }
        byte[] bytes = bytesOpt.get();

        int readPos = 0;
        if (bsController.byobRequest() != null) {
          ReadableStreamBYOBRequest byobRequest = bsController.byobRequest();
          ByteBuffer view = byobRequest.view();
          readPos = Math.min(bytes.length, view.limit());
          view.put(bytes, 0, readPos);
          bsController.byobRequest().respond(readPos);
        }

        if (readPos < bytes.length) {
          bsController.enqueue(ByteBuffer.wrap(bytes, readPos, bytes.length - readPos));
        }

        if (pullPromise.item != null) {
          pullPromise.item.complete(null);
          pullPromise.item = null;
        }
      });

      return null;
    };
    underlyingSource.pull = controller -> {
      return pullPromise.item = new CompletableFuture<>();
    };
    // TODO: Implement the cancel algo

    ReadableStream stream = ReadableStream.create(underlyingSource);
    response.setBody(new FetchBody(stream, null, 0));

    return response;
  }

  private static enum OverrideFetchType {
    SCHEME_FETCH, HTTP_FETCH
  }

  // Because Java.
  private static class FutureHolder {
    private CompletableFuture<Void> item;
  }

}
