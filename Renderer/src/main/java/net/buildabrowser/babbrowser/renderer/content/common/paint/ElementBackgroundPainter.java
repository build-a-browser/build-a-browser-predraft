package net.buildabrowser.babbrowser.renderer.content.common.paint;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;

public final class ElementBackgroundPainter {

  private static final boolean DEBUG_OUTLINES;

  static {
    DEBUG_OUTLINES = Boolean.getBoolean("babbrowser.debug");
  }
  
  private ElementBackgroundPainter() {}

  public static void paintBackground(
    PaintCanvas canvas,
    BoxFragment fragment,
    VpIntersection vpIntersection
  ) {
    ElementBackgroundImagePainter.paintBackgroundImages(
      canvas, fragment, vpIntersection,
      fragment.width(Measurement.BORDER),
      fragment.height(Measurement.BORDER));

    ElementBorderPainter.paintBorders(canvas, fragment);
    paintDebugOutlines(canvas, fragment);
  }

  public static void paintDebugOutlines(PaintCanvas canvas, BoxFragment fragment) {
    if (!DEBUG_OUTLINES) return;
    canvas.withPaint(
      p -> p.setColor(0xFFFF00FF),
      c -> {
        c.drawBox(0, 0, fragment.width(Measurement.BORDER), 1);
        c.drawBox(0, fragment.height(Measurement.BORDER) - 1, fragment.width(Measurement.BORDER), 1);
        c.drawBox(0, 0, 1, fragment.height(Measurement.BORDER));
        c.drawBox(fragment.width(Measurement.BORDER) - 1, 0, 1, fragment.height(Measurement.BORDER));
      });
  }

}
