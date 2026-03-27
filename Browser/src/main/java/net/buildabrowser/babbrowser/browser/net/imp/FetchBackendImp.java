package net.buildabrowser.babbrowser.browser.net.imp;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.fetch.FetchBackend;
import net.buildabrowser.babbrowser.fetch.FetchRequest;
import net.buildabrowser.babbrowser.fetch.mutable.MutableFetchResponse;

public class FetchBackendImp implements FetchBackend {

  private final HttpClient httpClient = HttpClient.newHttpClient();

  @Override
  public void makeRequest(
    MutableFetchResponse response,
    FetchRequest request,
    Consumer<Optional<byte[]>> byteConsumer
  ) {
    // TODO: Include the headers
    HttpRequest httpRequest = HttpRequest.newBuilder(request.url())
      .setHeader("User-Agent", "BABBrowser/0.1.0 Firefox/147.0 (Not actually Firefox)")
      // HTTP 2 seems broken on http
      .version(
        request.url().getScheme().equals("https") ? HttpClient.Version.HTTP_2 :
        HttpClient.Version.HTTP_1_1)
      .timeout(Duration.ofSeconds(5))
      .build();
    httpClient.sendAsync(httpRequest, responseInfo -> {
      // Wrapper allows for receiving responseInfo before the BodyHandler is done
      // TODO: Process the response info
      
      return BodyHandlers.ofByteArrayConsumer(byteConsumer).apply(responseInfo);
    }).exceptionally(e -> { e.printStackTrace(); return null; });
    // TODO: Proper exception handling
  }
  
}
