package net.buildabrowser.babbrowser.browser.network;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.buildabrowser.babbrowser.browser.network.exception.BadURLException;

public final class URLUtil {
  
  private URLUtil() {}

  public static URL createURL(String url) throws BadURLException {
    try {
      return new URI(url).toURL();
    } catch (MalformedURLException | URISyntaxException e) {
      throw new BadURLException(e);
    }
  }

}
