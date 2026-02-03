package net.buildabrowser.babbrowser.browser.render.content.common;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public final class PaddingUtil {
  
  private PaddingUtil() {}

  public static void computePadding(
    LayoutContext layoutContext, ElementBox childBox, LayoutConstraint referenceConstraint
  ) {
    ActiveStyles styles = childBox.activeStyles();
    float topPadding = computePadding(styles.getProperty(CSSProperty.PADDING_TOP), layoutContext, childBox, referenceConstraint);
    float bottomPadding = computePadding(styles.getProperty(CSSProperty.PADDING_BOTTOM), layoutContext, childBox, referenceConstraint);
    float leftPadding = computePadding(styles.getProperty(CSSProperty.PADDING_LEFT), layoutContext, childBox, referenceConstraint);
    float rightPadding = computePadding(styles.getProperty(CSSProperty.PADDING_RIGHT), layoutContext, childBox, referenceConstraint);
    childBox.dimensions().setComputedPadding(topPadding, bottomPadding, leftPadding, rightPadding);
  }

  private static float computePadding(
    CSSValue property, LayoutContext layoutContext, ElementBox childBox, LayoutConstraint referenceConstraint
  ) {
    LayoutConstraint constraint = SizingUtil.evaluateBaseSize(layoutContext, referenceConstraint, property);
    return constraint.isPreLayoutConstraint() ? 0 : constraint.value();
  }

}
