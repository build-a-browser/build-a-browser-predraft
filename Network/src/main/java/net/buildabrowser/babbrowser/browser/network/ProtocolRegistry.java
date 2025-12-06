package net.buildabrowser.babbrowser.browser.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface ProtocolRegistry {
  
  InputStream request(URL url) throws IOException;

}
