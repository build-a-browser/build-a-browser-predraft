package net.buildabrowser.babbrowser.browser.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.buildabrowser.babbrowser.browser.net.imp.ProtocolRegistryImp;

public interface ProtocolRegistry {
  
  InputStream request(URL url) throws IOException;

  static ProtocolRegistry create() {
    return new ProtocolRegistryImp();
  }

}
