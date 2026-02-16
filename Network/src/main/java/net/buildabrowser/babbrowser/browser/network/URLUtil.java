package net.buildabrowser.babbrowser.browser.network;

import java.net.URI;
import java.net.URISyntaxException;

import net.buildabrowser.babbrowser.browser.network.exception.BadURLException;

public final class URLUtil {
  
  private URLUtil() {}

  public static URI createURL(String url) throws BadURLException {
    try {
      return new URI(url);
    } catch (URISyntaxException e) {
      throw new BadURLException(e);
    }
  }

  public static URI createURL(URI base, String url) {
    return base.resolve(url);
  }

}
