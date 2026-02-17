package net.buildabrowser.babbrowser.browser.render.paint.java2d;

import java.awt.image.BufferedImage;

import net.buildabrowser.babbrowser.browser.render.paint.LoadedImage;

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
