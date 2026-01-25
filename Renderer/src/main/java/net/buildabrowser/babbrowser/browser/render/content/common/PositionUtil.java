package net.buildabrowser.babbrowser.browser.render.content.common;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.position.PositionValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public final class PositionUtil {
  
  public static void computeInsets(
    LayoutContext layoutContext,
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    PositionValue position = (PositionValue) childBox.activeStyles().getProperty(CSSProperty.POSITION);
    if (position.equals(PositionValue.RELATIVE)) {
      computeRelativeInsets(layoutContext, parentWidthConstraint, parentHeightConstraint, childBox);
    }
  }

  private static void computeRelativeInsets(
    LayoutContext layoutContext, 
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint,
    ElementBox childBox
  ) {
    ActiveStyles styles = childBox.activeStyles();
    int topInset = computeRelativeInset(
      styles.getProperty(CSSProperty.TOP), styles.getProperty(CSSProperty.BOTTOM),
      layoutContext, childBox, parentHeightConstraint);
    int leftInset = computeRelativeInset(
      styles.getProperty(CSSProperty.LEFT), styles.getProperty(CSSProperty.RIGHT),
      layoutContext, childBox, parentWidthConstraint);
    childBox.dimensions().setComputedInsets(topInset, -topInset, leftInset, -leftInset);
  }

  private static int computeRelativeInset(
    CSSValue startProperty,
    CSSValue endProperty,
    LayoutContext layoutContext,
    ElementBox childBox,
    LayoutConstraint referenceConstraint
  ) {
    LayoutConstraint startConstraint = SizingUtil.evaluateBaseSize(layoutContext, referenceConstraint, startProperty);
    LayoutConstraint endConstraint = SizingUtil.evaluateBaseSize(layoutContext, referenceConstraint, endProperty);
    
    boolean startConstraintIsAuto = startConstraint.equals(LayoutConstraint.AUTO);
    boolean endConstraintIsAuto = endConstraint.equals(LayoutConstraint.AUTO);
    if (startConstraintIsAuto && endConstraintIsAuto) {
      return 0;
    } else if (!startConstraintIsAuto) {
      return startConstraint.value();
    } else {
      return -endConstraint.value();
    }
  }

}
