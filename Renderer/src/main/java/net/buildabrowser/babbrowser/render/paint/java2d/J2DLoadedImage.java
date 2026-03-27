package net.buildabrowser.babbrowser.render.paint.java2d;

import java.awt.image.BufferedImage;

import net.buildabrowser.babbrowser.render.paint.LoadedImage;

public record J2DLoadedImage(BufferedImage image) implements LoadedImage {

  @Override
  public float width() {
    return image.getWidth();
  }

  @Override
  public float height() {
    return image.getHeight();
  }
  
}
