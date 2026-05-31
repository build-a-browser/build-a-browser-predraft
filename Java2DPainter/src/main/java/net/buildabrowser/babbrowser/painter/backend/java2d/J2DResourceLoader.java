package net.buildabrowser.babbrowser.painter.backend.java2d;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.buildabrowser.babbrowser.renderer.paint.backend.FontLoader;
import net.buildabrowser.babbrowser.renderer.paint.backend.LoadedImage;
import net.buildabrowser.babbrowser.renderer.paint.backend.ResourceLoader;

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
  public FontLoader fontLoader() {
    return this.fontLoader;
  }

}
