package net.buildabrowser.babbrowser.browser.network;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import net.buildabrowser.babbrowser.browser.network.exception.BadURLException;

public final class URLUtil {

  private static final URLStreamHandler NULL_URL_STREAM_HANDLER = new URLStreamHandler() {		
		@Override
		protected URLConnection openConnection(java.net.URL u) throws IOException {
			return null;
		}
	};
  
  private URLUtil() {}

  public static URL createURL(String url) throws BadURLException {
    try {
      return URL.of(new URI(url), NULL_URL_STREAM_HANDLER);
    } catch (MalformedURLException | URISyntaxException e) {
      throw new BadURLException(e);
    }
  }

  public static URL createURL(URL base, String url) throws BadURLException {
    try {
      return URL.of(base.toURI().resolve(url), NULL_URL_STREAM_HANDLER);
    } catch (MalformedURLException | URISyntaxException e) {
      throw new BadURLException(e);
    }
  }

}
