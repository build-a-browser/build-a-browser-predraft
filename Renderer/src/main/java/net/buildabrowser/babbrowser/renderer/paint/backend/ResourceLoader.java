package net.buildabrowser.babbrowser.renderer.paint.backend;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceLoader {
  
  LoadedImage loadImage(InputStream imageStream) throws IOException;

  FontLoader fontLoader();

}
