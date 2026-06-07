package net.buildabrowser.babbrowser.painter.skija;

import java.util.ArrayList;
import java.util.List;

import io.github.humbleui.skija.Path;
import io.github.humbleui.types.Point;
import net.buildabrowser.babbrowser.painter.core.ClipShapeSpec;

public class SkijaClipShapeSpec implements ClipShapeSpec {

  private final List<Point> points = new ArrayList<>(4);

  @Override
  public ClipShapeSpec addPoint(float x, float y) {
    points.add(new Point(x, y));
    return this;
  }

  public Path path() {
    return Path.makePolygon(points.toArray(Point[]::new), true);
  }

}
