package net.buildabrowser.babbrowser.renderer.content.common.paint;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderStyleValue;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue;
import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.painter.core.Paint;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment.Measurement;

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
    PropertyContainer properties = fragment.box().properties();

    CSSValue topStyle = properties.get(CSSProperty.BORDER_TOP_STYLE);
    CSSValue bottomStyle = properties.get(CSSProperty.BORDER_BOTTOM_STYLE);
    CSSValue leftStyle = properties.get(CSSProperty.BORDER_LEFT_STYLE);
    CSSValue rightStyle = properties.get(CSSProperty.BORDER_RIGHT_STYLE);

    float topBorderWidth = topStyle.equals(CSSValue.NONE) ? 0 : borders[0];
    float bottomBorderWidth = bottomStyle.equals(CSSValue.NONE) ? 0 : borders[1];
    float leftBorderWidth = leftStyle.equals(CSSValue.NONE) ? 0 : borders[2];
    float rightBorderWidth = rightStyle.equals(CSSValue.NONE) ? 0 : borders[3];
    
    // Top
    withBorderClipAndPaint(
      canvas,
      0, 0,
      fragmentWidth, topBorderWidth,
      leftBorderWidth, rightBorderWidth,
      BorderDirection.TOP,
      p -> p.setColor(borderColor(properties, CSSProperty.BORDER_TOP_COLOR)),
      c -> paintHorizontalBorder(
        c, 0, 0,
        fragmentWidth, topBorderWidth,
        (BorderStyleValue) topStyle));
    
    // Bottom
    float bottomBorderY = fragmentHeight - bottomBorderWidth;
    withBorderClipAndPaint(
      canvas,
      0, bottomBorderY,
      fragmentWidth, bottomBorderWidth,
      leftBorderWidth, rightBorderWidth,
      BorderDirection.BOTTOM,
      p -> p.setColor(borderColor(properties, CSSProperty.BORDER_BOTTOM_COLOR)),
      c -> paintHorizontalBorder(
        c, 0, bottomBorderY,
        fragmentWidth, bottomBorderWidth,
        (BorderStyleValue) bottomStyle));

    // Left
    withBorderClipAndPaint(
      canvas,
      0, 0,
      fragmentHeight, leftBorderWidth,
      topBorderWidth, bottomBorderWidth,
      BorderDirection.LEFT,
      p -> p.setColor(borderColor(properties, CSSProperty.BORDER_LEFT_COLOR)),
      c -> paintVerticalBorder(
        c, 0, 0,
        fragmentHeight, leftBorderWidth,
        (BorderStyleValue) leftStyle));

    // Right
    float rightBorderX = fragmentWidth - rightBorderWidth;
    withBorderClipAndPaint(
      canvas,
      rightBorderX, 0,
      fragmentHeight, rightBorderWidth,
      topBorderWidth, bottomBorderWidth,
      BorderDirection.RIGHT,
      p -> p.setColor(borderColor(properties, CSSProperty.BORDER_RIGHT_COLOR)),
      c -> paintVerticalBorder(
        c, rightBorderX, 0,
        fragmentHeight, rightBorderWidth,
        (BorderStyleValue) rightStyle));
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

  public static int borderColor(
    PropertyContainer properties,
    CSSProperty colorProperty
  ) {
    CSSValue property = properties.get(colorProperty);
    if (property.equals(CSSValue.NONE)) return PropertiesUtil.textColor(properties);
    return ((ColorValue) property).asSARGB();
  }

  private static void withBorderClipAndPaint(
    PaintCanvas canvas,
    float x, float y,
    float run, float thickness,
    float startWidth, float endWidth,
    BorderDirection borderDirection,
    Consumer<Paint> alterPaintFunc,
    Consumer<PaintCanvas> paintFunc
  ) {
    if (run <= 0 || thickness <= 0) return;
    
    canvas.withShapedClip(
      shape -> {
        switch (borderDirection) {
          case TOP -> shape
            .addPoint(x, y)
            .addPoint(x + run, y)
            .addPoint(x + run - endWidth, y + thickness)
            .addPoint(x + startWidth, y + thickness);
          case BOTTOM -> shape
            .addPoint(x + startWidth, y)
            .addPoint(x + run - endWidth, y)
            .addPoint(x + run, y + thickness)
            .addPoint(x, y + thickness);
          case LEFT -> shape
            .addPoint(x, y)
            .addPoint(x, y + run)
            .addPoint(x + thickness, y + run - endWidth)
            .addPoint(x + thickness, y + startWidth);
          case RIGHT -> shape
            .addPoint(x, y + startWidth)
            .addPoint(x, y + run - endWidth)
            .addPoint(x + thickness, y + run)
            .addPoint(x + thickness, y);
          default -> throw new UnsupportedOperationException(
            "Unrecognized border direction: " + borderDirection);
        }
      },
      c -> c.withPaint(alterPaintFunc, paintFunc));
  }

  public static enum BorderDirection {
    TOP, BOTTOM, LEFT, RIGHT;
  }

}
