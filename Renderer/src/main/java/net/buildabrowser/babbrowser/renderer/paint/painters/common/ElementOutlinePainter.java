package net.buildabrowser.babbrowser.renderer.paint.painters.common;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue;
import net.buildabrowser.babbrowser.cssbase.property.shared.LineStyleValue;
import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;

public final class ElementOutlinePainter {
  
  private ElementOutlinePainter() {}

  public static void paintOutlines(
    PaintCanvas canvas, BoxFragment<?> fragment
  ) {
    float fragmentWidth = fragment.width(Measurement.BORDER);
    float fragmentHeight = fragment.height(Measurement.BORDER);
    paintOutlines(canvas, fragment, fragmentWidth, fragmentHeight);
  }

  public static void paintOutlines(
    PaintCanvas canvas, BoxFragment<?> fragment,
    float fragmentWidth, float fragmentHeight
  ) {
    LayoutContext layoutContext = fragment.box().layoutContext();
    PropertyContainer properties = fragment.box().properties();

    CSSValue outlineStyleRaw = properties.get(CSSProperty.OUTLINE_STYLE);
    if (outlineStyleRaw.equals(CSSValue.NONE)) return;
    LineStyleValue outlineStyle =
      outlineStyleRaw.equals(CSSValue.AUTO) ? LineStyleValue.SOLID :
      (LineStyleValue) outlineStyleRaw;

    LayoutConstraint widthConstraint = SizingUtil.evaluateBaseSize(
      layoutContext, LayoutConstraint.AUTO,
      properties.get(CSSProperty.OUTLINE_WIDTH));
    float outlineWidth = widthConstraint.isBounded() ?
      widthConstraint.value() : 0;

    LayoutConstraint offsetConstraint = SizingUtil.evaluateBaseSize(
      layoutContext, LayoutConstraint.AUTO,
      properties.get(CSSProperty.OUTLINE_OFFSET));
    float offset = offsetConstraint.isBounded() ?
      offsetConstraint.value() : 0;

    float firstOffset = -outlineWidth - offset;
    float widthRun = outlineWidth * 2 + offset * 2 + fragmentWidth;
    float heightRun = fragmentHeight + offset * 2;
    
    canvas.withPaint(
      p -> p.setColor(outlineColor(properties)),
      c -> {
        // Top
        ElementBorderPainter.paintHorizontalBorder(
          c, firstOffset, firstOffset,
          widthRun, outlineWidth,
          outlineStyle);
        // Bottom
        ElementBorderPainter.paintHorizontalBorder(
          c, firstOffset, fragmentHeight + offset,
          widthRun, outlineWidth,
          outlineStyle);
        // Left
        ElementBorderPainter.paintVerticalBorder(
          c, firstOffset, -offset,
          heightRun, outlineWidth,
          outlineStyle);
        // Right
        ElementBorderPainter.paintVerticalBorder(
          c, fragmentWidth + offset, -offset,
          heightRun, outlineWidth,
          outlineStyle);
      });
  }

  public static int outlineColor(PropertyContainer properties) {
    // TODO: Also check accent color
    CSSValue property = properties.get(CSSProperty.OUTLINE_COLOR);
    if (property.equals(CSSValue.AUTO)) return PropertiesUtil.textColor(properties);
    return ((ColorValue) property).asSARGB();
  }

  public static enum BorderDirection {
    TOP, BOTTOM, LEFT, RIGHT;
  }

}
