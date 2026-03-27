package net.buildabrowser.babbrowser.network;

import java.net.URI;

import net.buildabrowser.babbrowser.network.exception.BadURLException;

public final class URLUtil {
  
  private URLUtil() {}

  public static URI createURL(String url) throws BadURLException {
    return URI.create(url);
  }

  public static URI createURL(URI base, String url) {
    return base.resolve(url);
  }

}
