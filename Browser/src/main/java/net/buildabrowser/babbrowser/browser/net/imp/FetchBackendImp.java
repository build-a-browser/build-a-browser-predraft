package net.buildabrowser.babbrowser.browser.net.imp;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.buildabrowser.babbrowser.browser.net.imp.FetchHostPool.QueuedRequest;
import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.fetch.FetchBackend;
import net.buildabrowser.babbrowser.fetch.FetchBody;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.FetchResponse;
import net.buildabrowser.babbrowser.fetch.HeaderList;
import net.buildabrowser.babbrowser.fetch.imp.FetchImpUtil;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchResponse;
import net.buildabrowser.babbrowser.network.ExtensionUtil;
import net.buildabrowser.babbrowser.network.URLUtil;
import net.buildabrowser.babbrowser.network.encoding.ContentDecoder;
import net.buildabrowser.babbrowser.network.encoding.ContentEncodingRegistry;
import net.buildabrowser.babbrowser.stream.ReadableStreamDefaultReader;

public class FetchBackendImp implements FetchBackend {

  // TODO: Need to determine 
  private static final int MAX_CONNECTIONS = 10;
  private static final String CHROME_UA_STRING
    = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)"
    + " Chrome/146.0.0.0 Safari/537.36";

  private static final Logger LOGGER = LoggerFactory.getLogger(FetchBackendImp.class);

  private final HttpClient httpClient;
  private final ContentEncodingRegistry encodingRegistry;
  private final Map<String, FetchHostPool> requestQueue = new HashMap<>();

  public FetchBackendImp(
    ContentEncodingRegistry encodingRegistry,
    ExecutorService executorService
  ) {
    this.encodingRegistry = encodingRegistry;
    this.httpClient = HttpClient.newBuilder()
      .executor(executorService)
      .build();
  }

  @Override
  public void makeRequest(
    MutableFetchResponse response,
    FetchRequest request,
    Consumer<Optional<ByteBuffer>> byteConsumer
  ) {
    String host = request.currentURL().getHost();
    boolean hasStream = false;
    synchronized (requestQueue) {
      FetchHostPool pool = requestQueue
        .computeIfAbsent(host, _1 -> new FetchHostPool(MAX_CONNECTIONS));
      hasStream = pool.acquireStream();
      if (!hasStream) {
        pool.queueRequest(new QueuedRequest(
          response, request, byteConsumer));
      }
    }

    if (hasStream) {
      makeRequestNow(response, request, byteConsumer);
    }
  }

  private void makeRequestNow(
    MutableFetchResponse response,
    FetchRequest request,
    Consumer<Optional<ByteBuffer>> byteConsumer
  ) {
    // TODO: Correct way to set origin
    URI url = request.currentURL();
    String origin = url.getScheme() + "://" + url.getHost();
    if (!(
      (url.getScheme().equals("https") && url.getPort() == 443)
      || (url.getScheme().equals("http") && url.getPort() == 80)
      || url.getPort() == -1
    )) {
      origin = origin + ":" + url.getPort();
    }
    
    BodyPublisher bodyPublisher = createBodyPublisher(request);
    HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder(url)
      .method(request.method(), bodyPublisher)
      .setHeader("User-Agent", chooseUserAgent(request))
      .setHeader("Accept", "text/html, text/css, image/png, image/jpeg, */*")
      .setHeader("Accept-Encoding", String.join(", ", encodingRegistry.acceptedEncodings()))
      .setHeader("Sec-CH-UA", "\"BuildABrowser Test Program\";v=\"0\"")
      .setHeader("Origin", origin)
      // HTTP 2 seems broken on http
      .version(
        url.getScheme().equals("https") ?
          HttpClient.Version.HTTP_2 :
          HttpClient.Version.HTTP_1_1)
      .timeout(Duration.ofSeconds(5));

    request.headerList().forEach(httpRequestBuilder::setHeader);

    HttpRequest httpRequest = httpRequestBuilder.build();
    httpClient.sendAsync(httpRequest, responseInfo -> {
      // Wrapper allows for receiving responseInfo before the BodyHandler is done
      response.urlList().add(request.currentURL()); // TODO: Is this handled elsewhere, for recursive requests?
      response.setStatus(responseInfo.statusCode());
      appendResponseHeaders(response, responseInfo);

      List<String> codings = response.headerList().extractHeaderListValues("Content-Encoding");
      ContentDecoder decoder = CommonUtil.rethrow(() -> encodingRegistry.createChainDecoder(codings,
        buffer -> byteConsumer.accept(Optional.of(buffer))));
      // TODO: Other parts of Fetch, maybe move this to the Fetch module
      
      return BodyHandlers.ofByteArrayConsumer(bytesOpt -> CommonUtil.rethrowV(() -> {
        if (bytesOpt.isPresent()) {
          decoder.push(ByteBuffer.wrap(bytesOpt.get()));
        } else {
          decoder.done();
          decoder.close();
          finishRequest(request);
          byteConsumer.accept(Optional.empty());
        }
      })).apply(responseInfo);
    }).exceptionally(e -> {
      LOGGER.error("An issue occured while handling a network packet!", e);
      finishRequest(request);
      return null;
    });
    // TODO: Proper exception handling
  }

  @Override
  public FetchResponse fetchFile(FetchRequest request) {
    // TODO: Improve security
    
    File file = CommonUtil.tryOrNull(() -> new File(URLUtil.stripFragment(request.url())));
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

  private void appendResponseHeaders(MutableFetchResponse response, ResponseInfo responseInfo) {
    HeaderList headerList = response.headerList();
    for (Map.Entry<String, List<String>> headerEntry: responseInfo.headers().map().entrySet()) {
      for (String value: headerEntry.getValue()) {
        headerList.append(headerEntry.getKey(), value);
      }
    }
  }

  private BodyPublisher createBodyPublisher(FetchRequest request) {
    if (request.body() == null) {
      return BodyPublishers.noBody();
    }

    FetchBody body = (FetchBody) request.body();
    ReadableStreamDefaultReader reader = (ReadableStreamDefaultReader)
      body.stream().getReader(null);
    return new StreamReaderBodyPublisher(body, reader);
  }

  private void finishRequest(FetchRequest request) {
    String host = request.currentURL().getHost();
    synchronized (requestQueue) {
      FetchHostPool pool = requestQueue.get(host);
      QueuedRequest queued = pool.unqueueRequest();
      if (queued == null) {
        pool.releaseStream();
        if (pool.hasNoData()) {
          requestQueue.remove(host);
        }
      }
      if (queued != null) {
        makeRequestNow(
          queued.response(),
          queued.request(),
          queued.byteConsumer());
      }
    }
  }

  // Unfortunately DDG captchas the user with the default UA (and captchas would require JS)
  private String chooseUserAgent(FetchRequest request) {
    // TODO: Report correct OS
    return switch (request.url().getHost()) {
      case "html.duckduckgo.com", "duckduckgo.com" -> CHROME_UA_STRING + " BABBrowser/0.1.0";
      // Unfortunately, HN just shows a page showing "sorry" half the time when using a proper UA string
      case "news.ycombinator.com" -> CHROME_UA_STRING;
      case "whatismybrowser.com", "www.whatismybrowser.com" -> "BABBrowser/0.1.0 (X11; Linux x86_64)";
      case "buildabrowser.net", "frogfind.de" -> "Mozilla/5.0 (X11; Linux x86_64) BABBrowser/0.1.0";
      default -> "Mozilla/5.0 (X11; Linux x86_64) BABBrowser/0.1.0 Firefox/149.0 (Not actually Firefox)";
    };
  }
  
}
