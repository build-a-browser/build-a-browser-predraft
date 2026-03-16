package net.buildabrowser.babbrowser.browser.render.paint;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceLoader {
  
  LoadedImage loadImage(InputStream imageStream) throws IOException;

  FontLoader fontLoader();

}
