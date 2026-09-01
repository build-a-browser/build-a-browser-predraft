package net.buildabrowser.babbrowser.painter.java2d;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import net.buildabrowser.babbrowser.painter.core.FontLoader;
import net.buildabrowser.babbrowser.painter.core.ImageLoader;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.painter.core.ProgressiveImageCallbacks;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;

public class J2DResourceLoader implements ResourceLoader {

  private final FontLoader fontLoader = new J2DFontLoader();

  @Override
  public LoadedImage loadImage(InputStream imageStream) throws IOException {
    BufferedImage image = ImageIO.read(imageStream);
    if (image == null) {
      throw new IOException("Failed to read image from stream");
    }
    return new J2DLoadedImage(image);
  }

  @Override
  public ImageLoader progressivelyLoadImage(
    String mimeType,
    ProgressiveImageCallbacks callbacks,
    Consumer<Runnable> threadRunner
  ) {
    return new Java2DImageLoader(mimeType, callbacks, threadRunner);
  }

  @Override
  public FontLoader fontLoader() {
    return this.fontLoader;
  }

}
