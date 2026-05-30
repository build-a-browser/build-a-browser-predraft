package net.buildabrowser.babbrowser.render.paint.backend.skija;

import java.io.IOException;
import java.io.InputStream;

import io.github.humbleui.skija.Image;
import net.buildabrowser.babbrowser.render.paint.backend.FontLoader;
import net.buildabrowser.babbrowser.render.paint.backend.LoadedImage;
import net.buildabrowser.babbrowser.render.paint.backend.ResourceLoader;

public class SkijaResourceLoader implements ResourceLoader {

  private final FontLoader fontLoader = new SkijaFontLoader();

  @Override
  public LoadedImage loadImage(InputStream imageStream) throws IOException {
    byte[] data = imageStream.readAllBytes();
    return new SkijaLoadedImage(Image.makeDeferredFromEncodedBytes(data));
  }

  @Override
  public FontLoader fontLoader() {
    return this.fontLoader;
  }

}
