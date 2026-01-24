package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.border.BorderStyleValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public final class FlowBorderUtil {
  
  private FlowBorderUtil() {}

  public static void computeBorder(
    LayoutContext layoutContext, ElementBox childBox, BlockFormattingContext referenceContext
  ) {
    ActiveStyles styles = childBox.activeStyles();
    LayoutConstraint referenceConstraint = referenceContext.innerWidthConstraint(); // TODO: Adjust in some contexts
    int topBorder = computeBorder(
      styles.getProperty(CSSProperty.BORDER_TOP_WIDTH), styles.getProperty(CSSProperty.BORDER_TOP_STYLE),
      layoutContext, childBox, referenceConstraint);
    int bottomBorder = computeBorder(
      styles.getProperty(CSSProperty.BORDER_BOTTOM_WIDTH), styles.getProperty(CSSProperty.BORDER_BOTTOM_STYLE),
      layoutContext, childBox, referenceConstraint);
    int leftBorder = computeBorder(
      styles.getProperty(CSSProperty.BORDER_LEFT_WIDTH), styles.getProperty(CSSProperty.BORDER_LEFT_STYLE),
      layoutContext, childBox, referenceConstraint);
    int rightBorder = computeBorder(
      styles.getProperty(CSSProperty.BORDER_RIGHT_WIDTH), styles.getProperty(CSSProperty.BORDER_RIGHT_STYLE),
      layoutContext, childBox, referenceConstraint);
    childBox.dimensions().setComputedBorder(topBorder, bottomBorder, leftBorder, rightBorder);
  }

  private static int computeBorder(
    CSSValue property, CSSValue styleProperty, LayoutContext layoutContext, ElementBox childBox, LayoutConstraint referenceConstraint
  ) {
    if (styleProperty.equals(CSSValue.NONE) || styleProperty.equals(BorderStyleValue.HIDDEN)) {
      return 0;
    }
    LayoutConstraint constraint = FlowWidthUtil.evaluateBaseSize(layoutContext, referenceConstraint, property);
    return constraint.isPreLayoutConstraint() ? 0 : Math.max(0, constraint.value());
  }

}
