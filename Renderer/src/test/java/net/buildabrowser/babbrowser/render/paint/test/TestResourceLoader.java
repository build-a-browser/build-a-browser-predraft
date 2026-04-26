package net.buildabrowser.babbrowser.render.paint.test;

import java.io.IOException;
import java.io.InputStream;

import net.buildabrowser.babbrowser.render.paint.backend.FontLoader;
import net.buildabrowser.babbrowser.render.paint.backend.LoadedFont;
import net.buildabrowser.babbrowser.render.paint.backend.LoadedImage;
import net.buildabrowser.babbrowser.render.paint.backend.ResourceLoader;

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
  public FontLoader fontLoader() {
    return fontLoader;
  }
  
}
