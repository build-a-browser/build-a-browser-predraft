package net.buildabrowser.babbrowser.render.content.common;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public final class PaddingUtil {
  
  private PaddingUtil() {}

  public static void computePadding(
    ElementBox childBox, LayoutConstraint referenceConstraint
  ) {
    ActiveStyles styles = childBox.activeStyles();
    float topPadding = computePadding(styles.getProperty(CSSProperty.PADDING_TOP), childBox, referenceConstraint);
    float bottomPadding = computePadding(styles.getProperty(CSSProperty.PADDING_BOTTOM), childBox, referenceConstraint);
    float leftPadding = computePadding(styles.getProperty(CSSProperty.PADDING_LEFT), childBox, referenceConstraint);
    float rightPadding = computePadding(styles.getProperty(CSSProperty.PADDING_RIGHT), childBox, referenceConstraint);
    childBox.dimensions().setComputedPadding(topPadding, bottomPadding, leftPadding, rightPadding);
  }

  private static float computePadding(
    CSSValue property, ElementBox childBox, LayoutConstraint referenceConstraint
  ) {
    LayoutConstraint constraint = SizingUtil.evaluateBaseSize(childBox.layoutContext(), referenceConstraint, property);
    return constraint.isPreLayoutConstraint() ? 0 : constraint.value();
  }

}
