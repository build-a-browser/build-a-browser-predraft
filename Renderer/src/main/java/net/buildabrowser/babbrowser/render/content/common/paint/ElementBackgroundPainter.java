package net.buildabrowser.babbrowser.render.content.common.paint;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public final class ElementBackgroundPainter {

  private static final boolean DEBUG_OUTLINES;

  static {
    DEBUG_OUTLINES = Boolean.getBoolean("babbrowser.debug");
  }
  
  private ElementBackgroundPainter() {}

  public static void paintBackground(PaintCanvas canvas, BoxFragment fragment) {
    ElementBackgroundImagePainter.paintBackgroundImages(
      canvas, fragment,
      fragment.width(Measurement.BORDER),
      fragment.height(Measurement.BORDER));

    ElementBorderPainter.paintBorders(canvas, fragment);
    paintDebugOutlines(canvas, fragment);
  }

  public static void paintDebugOutlines(PaintCanvas canvas, BoxFragment fragment) {
    if (!DEBUG_OUTLINES) return;
    canvas.alterPaint(paint -> paint.setColor(0xFFFF00FF));
    canvas.drawBox(0, 0, fragment.width(Measurement.BORDER), 1);
    canvas.drawBox(0, fragment.height(Measurement.BORDER) - 1, fragment.width(Measurement.BORDER), 1);
    canvas.drawBox(0, 0, 1, fragment.height(Measurement.BORDER));
    canvas.drawBox(fragment.width(Measurement.BORDER) - 1, 0, 1, fragment.height(Measurement.BORDER));
  }

}
