package net.buildabrowser.babbrowser.browser.render.paint.skija;

import io.github.humbleui.skija.Image;
import net.buildabrowser.babbrowser.browser.render.paint.LoadedImage;

public record SkijaLoadedImage(Image image) implements LoadedImage {

  @Override
  public float width() {
    return image.getWidth();
  }

  @Override
  public float height() {
    return image.getHeight();
  }
  
}
