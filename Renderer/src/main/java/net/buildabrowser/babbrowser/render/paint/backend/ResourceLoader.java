package net.buildabrowser.babbrowser.render.paint.backend;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceLoader {
  
  LoadedImage loadImage(InputStream imageStream) throws IOException;

  FontLoader fontLoader();

}
