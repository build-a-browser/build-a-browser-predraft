package net.buildabrowser.babbrowser.renderer.content.common;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderStyleValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class BorderUtil {
  
  private BorderUtil() {}

  public static void computeBorder(ElementBox childBox) {
    ActiveStyles styles = childBox.activeStyles();
    float topBorder = computeBorder(childBox,
      styles.getProperty(CSSProperty.BORDER_TOP_WIDTH), styles.getProperty(CSSProperty.BORDER_TOP_STYLE));
    float bottomBorder = computeBorder(childBox,
      styles.getProperty(CSSProperty.BORDER_BOTTOM_WIDTH), styles.getProperty(CSSProperty.BORDER_BOTTOM_STYLE));
    float leftBorder = computeBorder(childBox,
      styles.getProperty(CSSProperty.BORDER_LEFT_WIDTH), styles.getProperty(CSSProperty.BORDER_LEFT_STYLE));
    float rightBorder = computeBorder(childBox,
      styles.getProperty(CSSProperty.BORDER_RIGHT_WIDTH), styles.getProperty(CSSProperty.BORDER_RIGHT_STYLE));
    childBox.dimensions().setComputedBorder(topBorder, bottomBorder, leftBorder, rightBorder);
  }

  public static float computeBorder(
    ElementBox childBox,
    CSSValue property,
    CSSValue styleProperty
  ) {
    if (styleProperty.equals(CSSValue.NONE) || styleProperty.equals(BorderStyleValue.HIDDEN)) {
      return 0;
    }
    
    LayoutConstraint constraint = SizingUtil.evaluateBaseSize(childBox.layoutContext(), LayoutConstraint.AUTO, property);
    return constraint.isPreLayoutConstraint() ? 0 : Math.max(0, constraint.value());
  }

}
