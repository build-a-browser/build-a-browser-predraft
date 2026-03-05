package net.buildabrowser.babbrowser.browser.render.content.common.paint;

import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;

public final class ElementBackgroundPainter {
  
  private ElementBackgroundPainter() {}

  public static void paintBackground(PaintCanvas canvas, BoxFragment fragment) {
    canvas.alterPaint(paint -> paint.setColor(fragment.box().activeStyles().backgroundColor()));
    canvas.drawBox(0, 0, fragment.borderWidth(), fragment.borderHeight());

    // Might need changed when adding tables, but usually background is followed by border,
    // so it is easiest to just put here for now
    paintBorders(canvas, fragment);
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

    canvas.alterPaint(paint -> paint.setColor(fragment.box().activeStyles().borderLeftColor()));
    canvas.drawBox(fragment.borderWidth() - borders[3], 0, borders[3], fragment.borderHeight());
  }

}
