package net.buildabrowser.babbrowser.browser.render.paint.java2d;

import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.buildabrowser.babbrowser.browser.render.paint.LoadedImage;
import net.buildabrowser.babbrowser.browser.render.paint.ResourceLoader;

public class J2DResourceLoader implements ResourceLoader {

  @Override
  public LoadedImage loadImage(InputStream imageStream) throws IOException {
    return new J2DLoadedImage(ImageIO.read(imageStream));
  }

}
