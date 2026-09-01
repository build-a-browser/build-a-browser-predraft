package net.buildabrowser.babbrowser.renderer.paint.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.painter.core.FontLoader;
import net.buildabrowser.babbrowser.painter.core.ImageLoader;
import net.buildabrowser.babbrowser.painter.core.LoadedFont;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.painter.core.ProgressiveImageCallbacks;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;

public class TestResourceLoader implements ResourceLoader {

  private final FontLoader fontLoader;

  public TestResourceLoader(LoadedFont testFont) {
    this.fontLoader = new TestFontLoader(testFont);
  }

  @Override
  public LoadedImage loadImage(InputStream imageStream) throws IOException {
    throw new UnsupportedOperationException("Unimplemented method 'loadImage'");
  }

  @Override
  public ImageLoader progressivelyLoadImage(
    String mimeType,
    ProgressiveImageCallbacks callbacks,
    Consumer<Runnable> threadRunnerConsumer
  ) {
    throw new UnsupportedOperationException("Unimplemented method 'progressivelyLoadImage'");
  }

  @Override
  public FontLoader fontLoader() {
    return fontLoader;
  }
  
}
