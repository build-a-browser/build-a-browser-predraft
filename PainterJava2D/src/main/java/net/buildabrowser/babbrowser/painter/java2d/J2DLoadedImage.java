package net.buildabrowser.babbrowser.painter.java2d;

import java.awt.image.BufferedImage;

import net.buildabrowser.babbrowser.painter.core.LoadedImage;

public record J2DLoadedImage(BufferedImage image) implements LoadedImage {

  @Override
  public int width() {
    return image.getWidth();
  }

  @Override
  public int height() {
    return image.getHeight();
  }
  
}
