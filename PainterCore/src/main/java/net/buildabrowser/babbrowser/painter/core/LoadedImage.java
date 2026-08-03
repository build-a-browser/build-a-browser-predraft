package net.buildabrowser.babbrowser.painter.core;

import java.io.IOException;
import java.io.InputStream;

public interface LoadedImage {
 
  int width();

  int height();

  InputStream streamData() throws IOException;

}
