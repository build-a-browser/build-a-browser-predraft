package net.buildabrowser.babbrowser.network;

import java.net.URI;
import java.net.URISyntaxException;

public final class URLUtil {
  
  private URLUtil() {}

  public static URI createURL(String url) throws URISyntaxException {
    return URI.create(url);
  }

  public static URI createURL(URI base, String url) {
    return base.resolve(url);
  }

  public static URI stripFragment(URI url) throws URISyntaxException {
    return new URI(
      url.getScheme(),
      url.getUserInfo(),
      url.getHost(),
      url.getPort(),
      url.getPath(),
      url.getQuery(),
      null
    );
  }

}
