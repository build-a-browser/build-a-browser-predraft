package net.buildabrowser.babbrowser.render.content.common;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public final class MarginUtil {
  
  private MarginUtil() {}

  public static void computeSimpleMargin(
    ElementBox childBox, LayoutConstraint referenceConstraint
  ) {
    ActiveStyles styles = childBox.activeStyles();
    
    float topMargin = computeMargin(styles.getProperty(CSSProperty.MARGIN_TOP), childBox, referenceConstraint);
    float bottomMargin = computeMargin(styles.getProperty(CSSProperty.MARGIN_BOTTOM), childBox, referenceConstraint);
    childBox.dimensions().setComputedVerticalMargin(topMargin, bottomMargin);

    float leftMargin = computeMargin(styles.getProperty(CSSProperty.MARGIN_LEFT), childBox, referenceConstraint);
    float rightMargin = computeMargin(styles.getProperty(CSSProperty.MARGIN_RIGHT), childBox, referenceConstraint);
    childBox.dimensions().setComputedHorizontalMargin(leftMargin, rightMargin);
  }

  private static float computeMargin(
    CSSValue property,
    ElementBox childBox,
    LayoutConstraint referenceConstraint
  ) {
    LayoutConstraint constraint = SizingUtil.evaluateBaseSize(childBox.layoutContext(), referenceConstraint, property);
    return constraint.isPreLayoutConstraint() ? 0 : Math.max(0, constraint.value());
  }

}
