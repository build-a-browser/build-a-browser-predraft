package net.buildabrowser.babbrowser.render.content.common.paint;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderStyleValue;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public final class ElementBorderPainter {
  
  private ElementBorderPainter() {}

  public static void paintBorders(
    PaintCanvas canvas, BoxFragment fragment
  ) {
    float fragmentWidth = fragment.width(Measurement.BORDER);
    float fragmentHeight = fragment.height(Measurement.BORDER);
    paintBorders(canvas, fragment, fragmentWidth, fragmentHeight);
  }

  public static void paintBorders(
    PaintCanvas canvas, BoxFragment fragment,
    float fragmentWidth, float fragmentHeight
  ) {
    float[] borders = fragment.box().dimensions().getComputedBorder();
    ActiveStyles boxStyles = fragment.box().activeStyles();

    CSSValue topStyle = boxStyles.getProperty(CSSProperty.BORDER_TOP_STYLE);
    CSSValue bottomStyle = boxStyles.getProperty(CSSProperty.BORDER_BOTTOM_STYLE);
    CSSValue leftStyle = boxStyles.getProperty(CSSProperty.BORDER_LEFT_STYLE);
    CSSValue rightStyle = boxStyles.getProperty(CSSProperty.BORDER_RIGHT_STYLE);

    float topBorderWidth = topStyle.equals(CSSValue.NONE) ? 0 : borders[0];
    float bottomBorderWidth = bottomStyle.equals(CSSValue.NONE) ? 0 : borders[1];
    float leftBorderWidth = leftStyle.equals(CSSValue.NONE) ? 0 : borders[2];
    float rightBorderWidth = rightStyle.equals(CSSValue.NONE) ? 0 : borders[3];
    
    // Top
    canvas.alterPaint(paint -> paint.setColor(borderColor(boxStyles, CSSProperty.BORDER_TOP_COLOR)));
    clipBorder(
      canvas,
      0, 0,
      fragmentWidth, topBorderWidth,
      leftBorderWidth, rightBorderWidth,
      BorderDirection.TOP);
    if (topBorderWidth > 0) paintHorizontalBorder(
      canvas,
      0, 0,
      fragmentWidth, topBorderWidth,
      (BorderStyleValue) topStyle);
    unclipBorder(canvas);
    
    // Bottom
    float bottomBorderY = fragmentHeight - bottomBorderWidth;
    canvas.alterPaint(paint -> paint.setColor(borderColor(boxStyles, CSSProperty.BORDER_BOTTOM_COLOR)));
    clipBorder(
      canvas,
      0, bottomBorderY,
      fragmentWidth, bottomBorderWidth,
      leftBorderWidth, rightBorderWidth,
      BorderDirection.BOTTOM);
    if (bottomBorderWidth > 0) paintHorizontalBorder(
      canvas,
      0, bottomBorderY,
      fragmentWidth, bottomBorderWidth,
      (BorderStyleValue) bottomStyle);
    unclipBorder(canvas);

    // Left
    canvas.alterPaint(paint -> paint.setColor(borderColor(boxStyles, CSSProperty.BORDER_LEFT_COLOR)));
    clipBorder(
      canvas,
      0, 0,
      fragmentHeight, leftBorderWidth,
      topBorderWidth, bottomBorderWidth,
      BorderDirection.LEFT);
    if (leftBorderWidth > 0) paintVerticalBorder(
      canvas,
      0, 0,
      fragmentHeight, leftBorderWidth,
      (BorderStyleValue) leftStyle);
    unclipBorder(canvas);

    // Right
    float rightBorderX = fragmentWidth - rightBorderWidth;
    canvas.alterPaint(paint -> paint.setColor(borderColor(boxStyles, CSSProperty.BORDER_RIGHT_COLOR)));
    clipBorder(
      canvas,
      rightBorderX, 0,
      fragmentHeight, rightBorderWidth,
      topBorderWidth, bottomBorderWidth,
      BorderDirection.RIGHT);
    if (rightBorderWidth > 0) paintVerticalBorder(
      canvas,
      rightBorderX, 0,
      fragmentHeight, rightBorderWidth,
      (BorderStyleValue) rightStyle);
    unclipBorder(canvas);
  }

  // TODO: Need to respect styles
  public static void paintHorizontalBorder(
    PaintCanvas canvas,
    float x, float y,
    float run, float thickness,
    BorderStyleValue borderStyle
  ) {
    if (run <= 0 || thickness <= 0) return;
    canvas.drawBox(x, y, run, thickness);
  }

  public static void paintVerticalBorder(
    PaintCanvas canvas,
    float x, float y,
    float run, float thickness,
    BorderStyleValue borderStyle
  ) {
    canvas.drawBox(x, y, thickness, run);
  }

  public static int borderColor(ActiveStyles activeStyles, CSSProperty colorProperty) {
    CSSValue property = activeStyles.getProperty(colorProperty);
    if (property.equals(CSSValue.NONE)) return ActiveStylesUtil.textColor(activeStyles);
    return ((ColorValue) property).asSARGB();
  }

  private static void clipBorder(
    PaintCanvas canvas,
    float x, float y,
    float run, float thickness,
    float startWidth, float endWidth,
    BorderDirection borderDirection
  ) {
    if (run <= 0 || thickness <= 0) return;
  }

  private static void unclipBorder(PaintCanvas canvas) {
    
  }

  public static enum BorderDirection {
    TOP, BOTTOM, LEFT, RIGHT;
  }

}
