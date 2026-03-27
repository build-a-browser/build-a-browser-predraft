package net.buildabrowser.babbrowser.render.content.common;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderStyleValue;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public final class BorderUtil {
  
  private BorderUtil() {}

  public static void computeBorder(
    ElementBox childBox, LayoutConstraint referenceConstraint
  ) {
    ActiveStyles styles = childBox.activeStyles();
    float topBorder = computeBorder(
      styles.getProperty(CSSProperty.BORDER_TOP_WIDTH), styles.getProperty(CSSProperty.BORDER_TOP_STYLE),
      childBox, referenceConstraint);
    float bottomBorder = computeBorder(
      styles.getProperty(CSSProperty.BORDER_BOTTOM_WIDTH), styles.getProperty(CSSProperty.BORDER_BOTTOM_STYLE),
      childBox, referenceConstraint);
    float leftBorder = computeBorder(
      styles.getProperty(CSSProperty.BORDER_LEFT_WIDTH), styles.getProperty(CSSProperty.BORDER_LEFT_STYLE),
      childBox, referenceConstraint);
    float rightBorder = computeBorder(
      styles.getProperty(CSSProperty.BORDER_RIGHT_WIDTH), styles.getProperty(CSSProperty.BORDER_RIGHT_STYLE),
      childBox, referenceConstraint);
    childBox.dimensions().setComputedBorder(topBorder, bottomBorder, leftBorder, rightBorder);
  }

  private static float computeBorder(
    CSSValue property,
    CSSValue styleProperty,
    ElementBox childBox,
    LayoutConstraint referenceConstraint
  ) {
    if (styleProperty.equals(CSSValue.NONE) || styleProperty.equals(BorderStyleValue.HIDDEN)) {
      return 0;
    }
    
    LayoutConstraint constraint = SizingUtil.evaluateBaseSize(childBox.layoutContext(), referenceConstraint, property);
    return constraint.isPreLayoutConstraint() ? 0 : Math.max(0, constraint.value());
  }

}
