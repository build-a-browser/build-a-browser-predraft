package net.buildabrowser.babbrowser.painter.java2d;

import java.awt.Shape;
import java.awt.geom.Path2D;

import net.buildabrowser.babbrowser.painter.core.ClipShapeSpec;

public class J2DClipShapeSpec implements ClipShapeSpec {

  private final Path2D.Double shape = new Path2D.Double();
  
  private boolean opened = false;

  @Override
  public ClipShapeSpec addPoint(float x, float y) {
    if (opened) {
      shape.lineTo(x, y);
    } else {
      shape.moveTo(x, y);
      this.opened = true;
    }

    return this;
  }

  public Shape shape() {
    shape.closePath();
    return this.shape;
  }

}
