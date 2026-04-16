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
      .setHeader("User-Agent", chooseUserAgent(request))
      .setHeader("Accept", "text/html, text/css, image/png, image/jpeg, */*")
      // HTTP 2 seems broken on http
      .version(
        request.url().getScheme().equals("https") ?
          HttpClient.Version.HTTP_2 :
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

  // Unfortunately DDG captchas the user with the default UA (and captchas would require JS)
  private String chooseUserAgent(FetchRequest request) {
    // TODO: Report correct OS
    return switch (request.url().getHost()) {
      case "html.duckduckgo.com" -> "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 BABBrowser/0.1.0";
      case "www.whatismybrowser.com" -> "BABBrowser/0.1.0 (X11; Linux x86_64)";
      default -> "Mozilla/5.0 (X11; Linux x86_64) BABBrowser/0.1.0 Firefox/149.0 (Not actually Firefox)";
    };
      
      
  }
  
}
