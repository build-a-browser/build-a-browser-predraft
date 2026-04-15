package net.buildabrowser.babbrowser.render.content.common.paint;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.paint.PaintCanvas;

public final class ElementBackgroundPainter {

  private static final boolean DEBUG_OUTLINES;

  static {
    DEBUG_OUTLINES = Boolean.getBoolean("babbrowser.debug");
  }
  
  private ElementBackgroundPainter() {}

  public static void paintBackground(PaintCanvas canvas, BoxFragment fragment) {
    canvas.alterPaint(paint -> paint.setColor(fragment.box().activeStyles().backgroundColor()));
    canvas.drawBox(0, 0, fragment.borderWidth(), fragment.borderHeight());

    // Might need changed when adding tables, but usually background is followed by border,
    // so it is easiest to just put here for now
    paintBorders(canvas, fragment);

    if (DEBUG_OUTLINES) {
      paintDebugOutlines(canvas, fragment);
    }
  }

  private static void paintBorders(PaintCanvas canvas, BoxFragment fragment) {
    // Quick and dirty implementation, ignore styles for now
    float[] borders = fragment.box().dimensions().getComputedBorder();
    
    canvas.alterPaint(paint -> paint.setColor(fragment.box().activeStyles().borderTopColor()));
    canvas.drawBox(0, 0, fragment.borderWidth(), borders[0]);

    canvas.alterPaint(paint -> paint.setColor(fragment.box().activeStyles().borderBottomColor()));
    canvas.drawBox(0, fragment.borderHeight() - borders[1], fragment.borderWidth(), borders[1]);

    canvas.alterPaint(paint -> paint.setColor(fragment.box().activeStyles().borderLeftColor()));
    canvas.drawBox(0, 0, borders[2], fragment.borderHeight());

    canvas.alterPaint(paint -> paint.setColor(fragment.box().activeStyles().borderRightColor()));
    canvas.drawBox(fragment.borderWidth() - borders[3], 0, borders[3], fragment.borderHeight());
  }

  private static void paintDebugOutlines(PaintCanvas canvas, BoxFragment fragment) {
    canvas.alterPaint(paint -> paint.setColor(0xFFFF00FF));
    canvas.drawBox(0, 0, fragment.borderWidth(), 1);
    canvas.drawBox(0, fragment.borderHeight() - 1, fragment.borderWidth(), 1);
    canvas.drawBox(0, 0, 1, fragment.borderHeight());
    canvas.drawBox(fragment.borderWidth() - 1, 0, 1, fragment.borderHeight());
  }

}
