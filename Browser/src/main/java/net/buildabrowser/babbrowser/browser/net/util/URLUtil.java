package net.buildabrowser.babbrowser.browser.net.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import net.buildabrowser.babbrowser.browser.net.exception.BadURLException;

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
