package net.buildabrowser.babbrowser.render.content.common.paint;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue;
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
    canvas.alterPaint(paint -> paint.setColor(ActiveStylesUtil.backgroundColor(fragment.box().activeStyles())));
    canvas.drawBox(0, 0, fragment.width(Measurement.BORDER), fragment.height(Measurement.BORDER));

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
    
    canvas.alterPaint(paint -> paint.setColor(borderTopColor(fragment.box().activeStyles())));
    canvas.drawBox(0, 0, fragment.width(Measurement.BORDER), borders[0]);

    canvas.alterPaint(paint -> paint.setColor(borderBottomColor(fragment.box().activeStyles())));
    canvas.drawBox(0, fragment.height(Measurement.BORDER) - borders[1], fragment.width(Measurement.BORDER), borders[1]);

    canvas.alterPaint(paint -> paint.setColor(borderLeftColor(fragment.box().activeStyles())));
    canvas.drawBox(0, 0, borders[2], fragment.height(Measurement.BORDER));

    canvas.alterPaint(paint -> paint.setColor(borderRightColor(fragment.box().activeStyles())));
    canvas.drawBox(fragment.width(Measurement.BORDER) - borders[3], 0, borders[3], fragment.height(Measurement.BORDER));
  }

  private static void paintDebugOutlines(PaintCanvas canvas, BoxFragment fragment) {
    canvas.alterPaint(paint -> paint.setColor(0xFFFF00FF));
    canvas.drawBox(0, 0, fragment.width(Measurement.BORDER), 1);
    canvas.drawBox(0, fragment.height(Measurement.BORDER) - 1, fragment.width(Measurement.BORDER), 1);
    canvas.drawBox(0, 0, 1, fragment.height(Measurement.BORDER));
    canvas.drawBox(fragment.width(Measurement.BORDER) - 1, 0, 1, fragment.height(Measurement.BORDER));
  }

  public static int borderTopColor(ActiveStyles activeStyles) {
    CSSValue property = activeStyles.getProperty(CSSProperty.BORDER_TOP_COLOR);
    if (property.equals(CSSValue.NONE)) return ActiveStylesUtil.textColor(activeStyles);
    return ((ColorValue) property).asSARGB();
  }

  public static int borderBottomColor(ActiveStyles activeStyles) {
    CSSValue property = activeStyles.getProperty(CSSProperty.BORDER_BOTTOM_COLOR);
    if (property.equals(CSSValue.NONE)) return ActiveStylesUtil.textColor(activeStyles);
    return ((ColorValue) property).asSARGB();
  }

  public static int borderLeftColor(ActiveStyles activeStyles) {
    CSSValue property = activeStyles.getProperty(CSSProperty.BORDER_LEFT_COLOR);
    if (property.equals(CSSValue.NONE)) return ActiveStylesUtil.textColor(activeStyles);
    return ((ColorValue) property).asSARGB();
  }

  public static int borderRightColor(ActiveStyles activeStyles) {
    CSSValue property = activeStyles.getProperty(CSSProperty.BORDER_RIGHT_COLOR);
    if (property.equals(CSSValue.NONE)) return ActiveStylesUtil.textColor(activeStyles);
    return ((ColorValue) property).asSARGB();
  }

}
