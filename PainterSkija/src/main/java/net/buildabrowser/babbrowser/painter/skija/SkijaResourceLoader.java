package net.buildabrowser.babbrowser.painter.skija;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import io.github.humbleui.skija.Image;
import net.buildabrowser.babbrowser.painter.core.FontLoader;
import net.buildabrowser.babbrowser.painter.core.ImageLoader;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.painter.core.ProgressiveImageCallbacks;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;

public class SkijaResourceLoader implements ResourceLoader {

  private final FontLoader fontLoader = new SkijaFontLoader();

  @Override
  public LoadedImage loadImage(InputStream imageStream) throws IOException {
    byte[] data = imageStream.readAllBytes();
    return new SkijaLoadedImage(Image.makeDeferredFromEncodedBytes(data));
  }

  @Override
  public ImageLoader progressivelyLoadImage(
    String mimeType,
    ProgressiveImageCallbacks callbacks,
    Consumer<Runnable> threadRunner
  ) {
    return new SkijaImageLoader(callbacks);
  }

  @Override
  public FontLoader fontLoader() {
    return this.fontLoader;
  }

}
