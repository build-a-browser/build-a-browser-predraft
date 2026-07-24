package net.buildabrowser.babbrowser.fetch.imp;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.slice;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.fetch.FetchBody;
import net.buildabrowser.babbrowser.fetch.FetchConfig;
import net.buildabrowser.babbrowser.fetch.FetchController;
import net.buildabrowser.babbrowser.fetch.FetchDestinatation;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.fetch.FetchParameters;
import net.buildabrowser.babbrowser.fetch.FetchParameters.ProcessResponse;
import net.buildabrowser.babbrowser.fetch.FetchParameters.ProcessResponseConsumeBody;
import net.buildabrowser.babbrowser.fetch.FetchParams;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.FetchRequest.RedirectMode;
import net.buildabrowser.babbrowser.fetch.FetchRequest.RequestMode;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.fetch.FetchUtil;
import net.buildabrowser.babbrowser.fetch.HeaderList;
import net.buildabrowser.babbrowser.fetch.imp.DataURLProcessor.DataURL;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchRequest;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchResponse;
import net.buildabrowser.babbrowser.stream.ReadableByteStreamController;
import net.buildabrowser.babbrowser.stream.ReadableStream;
import net.buildabrowser.babbrowser.stream.ReadableStreamBYOBRequest;
import net.buildabrowser.babbrowser.stream.ReadableStreamController;
import net.buildabrowser.babbrowser.stream.ReadableStreamDefaultReader;
import net.buildabrowser.babbrowser.stream.UnderlyingSource;
import net.buildabrowser.babbrowser.stream.UnderlyingSource.ReadableStreamType;

public class FetchEngineImp implements FetchEngine {

  private static final List<String> REQUEST_BODY_HEADER_NAMES = List.of(
    "Content-Encoding", "Content-Language", "Content-Location", "Content-Type");

  private final FetchConfig fetchConfig;

  public FetchEngineImp(FetchConfig fetchConfig) {
    this.fetchConfig = fetchConfig;
  }

  @Override
  public FetchController fetch(FetchParameters fetchParameters) {
    // TODO: A ton of random stuff
    MutableFetchRequest request = fetchParameters.request;
    FetchDestinatation taskDestination = null;
    if (request.client() != null) {
      taskDestination = request.client().fetchDestinatation();
    }
    FetchParams fetchParams = new FetchParams(
      request,
      fetchParameters.processResponse,
      fetchParameters.processResponseConsumeBody,
      taskDestination, new FetchController());
    if (request.body() instanceof ByteBuffer) {
      request.setBody(
        FetchImpUtil.safelyExtractABodyWithType(request.body()).body());
    }
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
      default -> throw new UnsupportedOperationException("Unrecognized fetch type: " + fetchType);
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
    return fetchConfig.backend().fetchFile(request);
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

    if (
      response.status() != 303
      && request.body() != null
      && ((FetchBody) request.body()).source() == null
    ) {
      return FetchResponse.createNetworkError();
    }

    if (
      ((response.status() == 301 || response.status() == 302)
        && request.method().equals("POST"))
      || (response.status() == 303
        && !(request.method().equals("GET") || request.method().equals("HEAD")))
    ) {
      request.setMethod("GET");
      request.setBody(null);
      for (String headerName: REQUEST_BODY_HEADER_NAMES) {
        request.headerList().delete(headerName);
      }
    }

    // TODO: CORS stuff

    if (request.body() != null) {
      request.setBody(FetchImpUtil.safelyExtractABodyWithType(
        ((FetchBody) request.body()).source()).body());
    }

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
    FetchRequest request = fetchParams.request();
    FetchRequest httpRequest = request; // TODO: Set properly
    // TODO: A ton of stuff
    boolean includeCredentials = true; // TODO: Set properly
    if (includeCredentials) {
      FetchCookieUtil.appendRequestCookieHeader(fetchConfig, httpRequest);
    }
    return httpNetworkFetch(
      fetchParams, includeCredentials, isNewConnectionFetch);
  }

  private FetchResponse httpNetworkFetch(
    FetchParams fetchParams,
    boolean includeCredentials,
    boolean forceNewConnection
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
      fetchConfig.backend().makeRequest(response, request, bytesOpt -> {
        // Avoid race conditions from parallel execution
        // TODO: Is this fine to move to the fetch task queue?
        // Since the surrounding code is running in parallel, the CompletableFuture is not a problem
        // But I don't know if the buffer we have at this point is one that could be overwritten by HttpClient
        // if there was no decompression processing
        fetchParams.taskDestination().queueFetchTask(
          () -> queueChunkToStream(receivedResponse, pullPromise, controller, bytesOpt));
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

    if (includeCredentials) {
      FetchCookieUtil.parseAndStoreResponseSetCookieHeaders(
        fetchConfig, request, response);
    }

    return response;
  }

  private void queueChunkToStream(
    CompletableFuture<Void> receivedResponse,
    FutureHolder pullPromise,
    ReadableStreamController controller,
    Optional<ByteBuffer> bytesOpt
  ) {
    receivedResponse.complete(null);
    ReadableByteStreamController bsController = (ReadableByteStreamController) controller;
    if (!bytesOpt.isPresent()) {
      bsController.close();
      return;
    }
    ByteBuffer bytes = bytesOpt.get();

    int readLen = 0;
    if (bsController.byobRequest() != null) {
      ReadableStreamBYOBRequest byobRequest = bsController.byobRequest();
      ByteBuffer view = byobRequest.view();
      readLen = Math.min(bytes.remaining(), view.remaining());
      view.put(slice(bytes, bytes.position(), readLen));
      ((Buffer) view).flip();
      ((Buffer) bytes).position(bytes.position() + readLen);
      bsController.byobRequest().respond(readLen);
    }

    if (bytes.remaining() > 0) {
      bsController.enqueue(slice(bytes, bytes.position(), bytes.remaining()));
      ((Buffer) bytes).position(bytes.limit());
    }

    if (pullPromise.item != null) {
      pullPromise.item.complete(null);
      pullPromise.item = null;
    }
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
