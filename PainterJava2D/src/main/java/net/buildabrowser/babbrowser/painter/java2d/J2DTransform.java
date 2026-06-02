package net.buildabrowser.babbrowser.painter.java2d;

import java.awt.Graphics2D;

import net.buildabrowser.babbrowser.painter.core.Transform;

public class J2DTransform implements Transform {

  private final Graphics2D graphics;

  public J2DTransform(Graphics2D graphics) {
    this.graphics = graphics;
  }

  @Override
  public void translate(float x, float y) {
    graphics.translate(x, y);
  }

  @Override
  public void scale(float scalingX, float scalingY) {
    graphics.scale(scalingX, scalingY);
  }
  
}
