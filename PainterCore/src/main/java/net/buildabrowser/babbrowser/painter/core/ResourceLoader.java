package net.buildabrowser.babbrowser.painter.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public interface ResourceLoader {
  
  LoadedImage loadImage(InputStream imageStream) throws IOException;

  ImageLoader progressivelyLoadImage(
    String mimeType,
    ProgressiveImageCallbacks callbacks,
    Consumer<Runnable> threadRunner
  );

  FontLoader fontLoader();

}
