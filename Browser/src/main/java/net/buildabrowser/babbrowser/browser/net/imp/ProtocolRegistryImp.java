package net.buildabrowser.babbrowser.browser.net.imp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.browser.network.ProtocolRegistry;

public class ProtocolRegistryImp implements ProtocolRegistry {

  private final Map<String, ProtocolRegistration> registeredProtocols = new HashMap<>();
  
  public ProtocolRegistryImp() {
    registeredProtocols.put("file", url -> new FileInputStream(url.getPath()));
    registeredProtocols.put("http", url -> openHTTPConnection(url));
    registeredProtocols.put("https", url -> openHTTPConnection(url));
  }

  private InputStream openHTTPConnection(URI url) throws IOException, URISyntaxException {
    URLConnection connection = url.toURL().openConnection();
    connection.setRequestProperty("User-Agent", "BABBrowser/0.1.0 Firefox/147.0 (Not actually Firefox)");
    
    return connection.getInputStream();
  }

  @Override
  public InputStream request(URI url) throws IOException {
    try {
      return registeredProtocols.get(url.getScheme()).request(url);
    } catch (URISyntaxException e) {
      throw new IOException(e);
    }
  }

  private static interface ProtocolRegistration {
  
    InputStream request(URI url) throws IOException, URISyntaxException;
    
  }
  
}
