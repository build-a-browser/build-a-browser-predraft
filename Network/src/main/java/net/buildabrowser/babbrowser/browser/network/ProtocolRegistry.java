package net.buildabrowser.babbrowser.browser.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public interface ProtocolRegistry {
  
  InputStream request(URI url) throws IOException;

}
