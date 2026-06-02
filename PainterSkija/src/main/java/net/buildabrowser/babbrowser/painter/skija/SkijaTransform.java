package net.buildabrowser.babbrowser.painter.skija;

import io.github.humbleui.skija.Canvas;
import net.buildabrowser.babbrowser.painter.core.Transform;

public class SkijaTransform implements Transform{

  private final Canvas canvas;

  public SkijaTransform(Canvas canvas) {
    this.canvas = canvas;
  }

  @Override
  public void translate(float x, float y) {
    canvas.translate(x, y);
  }

  @Override
  public void scale(float scalingX, float scalingY) {
    canvas.scale(scalingX, scalingY);
  }

}
