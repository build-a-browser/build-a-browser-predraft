package net.buildabrowser.babbrowser.browser.net.imp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.browser.net.ProtocolRegistry;

public class ProtocolRegistryImp implements ProtocolRegistry {

  private final Map<String, ProtocolRegistration> registeredProtocols = new HashMap<>();
  
  public ProtocolRegistryImp() {
    registeredProtocols.put("file", url -> new FileInputStream(url.getPath()));
    registeredProtocols.put("http", url -> url.openConnection().getInputStream());
    registeredProtocols.put("https", url -> url.openConnection().getInputStream());
  }

  @Override
  public InputStream request(URL url) throws IOException {
    return registeredProtocols.get(url.getProtocol()).request(url);
  }

  private static interface ProtocolRegistration {
  
    InputStream request(URL url) throws IOException;
    
  }
  
}
