package net.buildabrowser.babbrowser.fetch.imp;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.fetch.FetchBackend;
import net.buildabrowser.babbrowser.fetch.FetchBody;
import net.buildabrowser.babbrowser.fetch.FetchController;
import net.buildabrowser.babbrowser.fetch.FetchDestinatation;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.fetch.FetchParameters;
import net.buildabrowser.babbrowser.fetch.FetchParameters.ProcessResponse;
import net.buildabrowser.babbrowser.fetch.FetchParameters.ProcessResponseConsumeBody;
import net.buildabrowser.babbrowser.fetch.FetchRequest.RedirectMode;
import net.buildabrowser.babbrowser.fetch.FetchRequest.RequestMode;
import net.buildabrowser.babbrowser.fetch.FetchParams;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.fetch.FetchUtil;
import net.buildabrowser.babbrowser.fetch.HeaderList;
import net.buildabrowser.babbrowser.fetch.imp.DataURLProcessor.DataURL;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchResponse;
import net.buildabrowser.babbrowser.network.ExtensionUtil;
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
  public FetchController fetch(FetchParameters fetchParameters) {
    // TODO: A ton of random stuff
    FetchRequest request = fetchParameters.request;
    FetchDestinatation taskDestination = null;
    if (request.client() != null) {
      taskDestination = request.client().fetchDestinatation();
    }
    FetchParams fetchParams = new FetchParams(
      request,
      fetchParameters.processResponse,
      fetchParameters.processResponseConsumeBody,
      taskDestination, new FetchController());
    mainFetch(fetchParams, false);

    return fetchParams.controller();
  }

  private FetchResponse mainFetch(FetchParams fetchParams, boolean recursive) {
    if (!recursive) {
      fetchParams.request().client().fetchDestinatation().runInParallel(
        () -> mainFetchParallel(fetchParams, recursive));
      return null;
    } else {
      return mainFetchParallel(fetchParams, recursive);
    }
  }
  
  private FetchResponse mainFetchParallel(FetchParams fetchParams, boolean recursive) {
    FetchRequest request = fetchParams.request();
    FetchResponse response = null;
    // TODO: A ton of random stuff
    if (response == null) {
      response = mainFetchChain(fetchParams);
    }
    if (recursive) return response;

    if(response.urlList().isEmpty()) {
      response.urlList().addAll(request.urlList());
    }

    fetchResponseHandover(fetchParams, response);
    return null;
  }

  private FetchResponse mainFetchChain(FetchParams fetchParams) {
    FetchRequest request = fetchParams.request();
    // TODO: More cases
    if (
      // TODO: Right now I'm special-casing these, but it should instead do scheme upon some-origin or no-cors
      !request.currentURL().getScheme().equals("http")
      && !request.currentURL().getScheme().equals("https")
    ) {
      // TODO: Response tainting, also more options in the if
      return overrideFetch(OverrideFetchType.SCHEME_FETCH, fetchParams);
    } else {
      return overrideFetch(OverrideFetchType.HTTP_FETCH, fetchParams);
    }
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
        if ("blank".equals(request.currentURL().getSchemeSpecificPart())) {
          return FetchResponse.create(
            "OK", HeaderList.create("Content-Type", "text/html;charset=utf-8"),
            FetchImpUtil.getBytesAsABody(new byte[0]));
        }
        break;
      case "blob":
        // TODO: Implement blob
        break;
      case "data": return fetchData(request);
      case "file": return fetchFile(request);
      case "http", "https":
        return httpFetch(fetchParams, false);
      default:
        break;
    }

    return FetchResponse.createNetworkError();
  }

  private FetchResponse fetchData(FetchRequest request) {
    DataURL dataURL = DataURLProcessor.processDataURL(request.currentURL());
    if (dataURL == null) return FetchResponse.createNetworkError();
    String mimeType = dataURL.mimeType();
    return FetchResponse.create(
      "OK",
      HeaderList.create("Content-Type", mimeType),
      FetchImpUtil.getBytesAsABody(dataURL.body()));
  }

  private FetchResponse fetchFile(FetchRequest request) {
    // The spec does not say how to implement file
    // TODO: Improve security
    File file = CommonUtil.tryOrNull(() -> new File(request.url()));
    if (file == null || !file.exists() || file.isDirectory()) {
      return FetchResponse.createNetworkError();
    }
    
    try {
      byte[] bytes = Files.readAllBytes(file.toPath());
      String mimeType = ExtensionUtil.guessMimeTypeFromFileName(file.getPath());
      if (mimeType == null) {
        mimeType = "application/octet-stream";
      }
      return FetchResponse.create(
        "OK",
        HeaderList.create("Content-Type", mimeType),
        FetchImpUtil.getBytesAsABody(bytes));
    } catch (IOException e) {
      return FetchResponse.createNetworkError();
    }
  }

  private FetchResponse httpFetch(FetchParams fetchParams, boolean makeCORSPreflight) {
    // TODO: A ton of stuff
    FetchRequest request = fetchParams.request();
    FetchResponse internalResponse = httpNetworkOrCacheFetch(fetchParams, false, false);
    FetchResponse response = internalResponse;
    if (FetchUtil.isRedirectStatus(internalResponse.status())) {
      switch (request.redirectMode()) {
        case ERROR -> response = FetchResponse.createNetworkError();
        case MANUAL -> {
          if (request.mode().equals(RequestMode.NAVIGATE)) {
            // internalResponse should be the same as response
            fetchParams.controller().nextManualRedirectSteps = () -> httpRedirectFetch(fetchParams, internalResponse);
          }
          // TODO: Fallback
        }
        case FOLLOW -> response = httpRedirectFetch(fetchParams, response);
      }
    }

    return response;
  }

  private FetchResponse httpRedirectFetch(FetchParams fetchParams, FetchResponse response) {
    MutableFetchRequest request = (MutableFetchRequest) fetchParams.request();
    URI locationURL;
    try {
      locationURL = response.locationURL(request.currentURL().getFragment());
      if (locationURL == null) return response;
    } catch (URISyntaxException e) {
      return FetchResponse.createNetworkError();
    }

    if (!FetchUtil.isHTTPScheme(locationURL.getScheme())) {
      return FetchResponse.createNetworkError();
    }

    // Spec says == 20, but using >= 20 just in case
    if (request.redirectCount() >= 20) {
      return FetchResponse.createNetworkError();
    }
    request.increaseRedirectCount();

    // TODO: A ton of stuff

    request.appendURL(locationURL);

    boolean recursive = true;
    if (request.redirectMode().equals(RedirectMode.MANUAL)) {
      assert request.mode().equals(RequestMode.NAVIGATE);
      recursive = false;
    }
    return mainFetch(fetchParams, recursive);
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

    CompletableFuture<Void> receivedResponse = new CompletableFuture<>();

    FutureHolder pullPromise = new FutureHolder();
    UnderlyingSource underlyingSource = new UnderlyingSource();
    underlyingSource.type = ReadableStreamType.BYTES;
    underlyingSource.start = controller -> {
      // TODO: The spec defines the stream as a pull source, but it's easier to implement as a push source for now
      // Come back to this later and correct it.
      fetchBackend.makeRequest(response, request, bytesOpt -> {
        receivedResponse.complete(null);
        ReadableByteStreamController bsController = (ReadableByteStreamController) controller;
        if (bytesOpt.isEmpty()) {
          bsController.close();
          return;
        }
        ByteBuffer bytes = bytesOpt.get();

        int readLen = 0;
        if (bsController.byobRequest() != null) {
          ReadableStreamBYOBRequest byobRequest = bsController.byobRequest();
          ByteBuffer view = byobRequest.view();
          readLen = Math.min(bytes.remaining(), view.remaining());
          view.put(bytes.slice(bytes.position(), readLen));
          view.flip();
          bytes.position(bytes.position() + readLen);
          bsController.byobRequest().respond(readLen);
        }

        if (bytes.remaining() > 0) {
          bsController.enqueue(bytes.slice(bytes.position(), bytes.remaining()));
          bytes.position(bytes.limit());
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

    // TODO: Need to handle 1xx
    CommonUtil.rethrowV(() -> receivedResponse.get());

    return response;
  }

  private void fetchResponseHandover(FetchParams fetchParams, FetchResponse response) {
    // TODO: Other stuff
    ProcessResponse processResponse = fetchParams.processResponse();
    if (processResponse != null) {
      fetchParams.taskDestination().queueFetchTask(() -> processResponse.run(response));
    }

    ProcessResponseConsumeBody consumeBody = fetchParams.processResponseConsumeBody();
    if (fetchParams.processResponseConsumeBody() != null) {
      if (response.body() == null) {
        fetchParams.taskDestination().queueFetchTask(() -> consumeBody.run(response, true, null));
      } else {
        fullyRead(
          response.body(),
          bytes -> consumeBody.run(response, true, bytes),
          () -> consumeBody.run(response, false, null),
          fetchParams.taskDestination());
      }
    }
  }

  private void fullyRead(
    FetchBody body, Consumer<byte[]> processBody, Runnable processBodyError, FetchDestinatation taskDestination
  ) {
    // TODO: Obtaining the reader itself is not meant to be in the fetch queue, but I get race conditions because
    // the code is running in parallel at this point
    taskDestination.queueFetchTask(() -> {
      ReadableStreamDefaultReader reader = (ReadableStreamDefaultReader) body.stream().getReader(null);
      reader.readAllBytes(
        bytes -> taskDestination.queueFetchTask(() -> processBody.accept(bytes)),
        bytes -> taskDestination.queueFetchTask(() -> processBodyError.run()));
    });
  }

  private static enum OverrideFetchType {
    SCHEME_FETCH, HTTP_FETCH
  }

  // Because Java.
  private static class FutureHolder {
    private CompletableFuture<Void> item;
  }

}
