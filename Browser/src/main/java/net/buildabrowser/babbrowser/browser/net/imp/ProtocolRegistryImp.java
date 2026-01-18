package net.buildabrowser.babbrowser.browser.net.imp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

  private InputStream openHTTPConnection(URL url) throws IOException {
    URLConnection connection = url.openConnection();
    connection.setRequestProperty("User-Agent", "BABBrowser/0.1.0 Firefox/147.0 (Not actually Firefox)");
    
    return connection.getInputStream();
  }

  @Override
  public InputStream request(URL url) throws IOException {
    return registeredProtocols.get(url.getProtocol()).request(url);
  }

  private static interface ProtocolRegistration {
  
    InputStream request(URL url) throws IOException;
    
  }
  
}
