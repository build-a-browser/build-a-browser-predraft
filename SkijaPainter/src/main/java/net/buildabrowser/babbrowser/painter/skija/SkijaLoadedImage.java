package net.buildabrowser.babbrowser.painter.skija;

import io.github.humbleui.skija.Image;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;

public record SkijaLoadedImage(Image image) implements LoadedImage {

  @Override
  public int width() {
    return image.getWidth();
  }

  @Override
  public int height() {
    return image.getHeight();
  }
  
}
