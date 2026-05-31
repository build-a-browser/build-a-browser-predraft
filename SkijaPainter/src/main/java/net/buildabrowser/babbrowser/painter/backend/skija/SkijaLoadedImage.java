package net.buildabrowser.babbrowser.painter.backend.skija;

import io.github.humbleui.skija.Image;
import net.buildabrowser.babbrowser.renderer.paint.backend.LoadedImage;

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
