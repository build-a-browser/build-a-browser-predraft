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

}
