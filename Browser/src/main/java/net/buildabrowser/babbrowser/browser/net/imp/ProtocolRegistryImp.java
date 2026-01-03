package net.buildabrowser.babbrowser.browser.net.imp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.browser.network.ProtocolRegistry;

public class ProtocolRegistryImp implements ProtocolRegistry {

  private final Map<String, ProtocolRegistration> registeredProtocols = new HashMap<>();
  
  public ProtocolRegistryImp() {
    registeredProtocols.put("file", url -> new FileInputStream(url.getPath()));
    registeredProtocols.put("http", url -> url.toURI().toURL().openConnection().getInputStream());
    registeredProtocols.put("https", url -> url.toURI().toURL().openConnection().getInputStream());
  }

  @Override
  public InputStream request(URL url) throws IOException {
    try {
      return registeredProtocols.get(url.getProtocol()).request(url);
    } catch (URISyntaxException e) {
      throw new IOException(e);
    }
  }

  private static interface ProtocolRegistration {
  
    InputStream request(URL url) throws IOException, URISyntaxException;
    
  }
  
}
