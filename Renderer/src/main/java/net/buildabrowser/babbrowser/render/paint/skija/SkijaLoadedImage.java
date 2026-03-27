package net.buildabrowser.babbrowser.render.paint.skija;

import io.github.humbleui.skija.Image;
import net.buildabrowser.babbrowser.render.paint.LoadedImage;

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
