package net.buildabrowser.babbrowser.browser.render.content.common.position;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.position.PositionValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public final class PositionUtil {

  public static boolean affectsLayout(ElementBox box) {
    CSSValue position = box.activeStyles().getProperty(CSSProperty.POSITION);
    return position.equals(PositionValue.STATIC) || position.equals(PositionValue.RELATIVE);
  }
  
  public static LayoutConstraint[] computeInsets(
    LayoutContext layoutContext,
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    PositionValue position = (PositionValue) childBox.activeStyles().getProperty(CSSProperty.POSITION);
    if (position.equals(PositionValue.RELATIVE)) {
      return computeRelativeInsets(layoutContext, parentWidthConstraint, parentHeightConstraint, childBox);
    } else {
      return computeAbsoluteInsets(layoutContext, parentWidthConstraint, parentHeightConstraint, childBox);
    }
  }

  private static LayoutConstraint[] computeRelativeInsets(
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
    
    return new LayoutConstraint[] {
      LayoutConstraint.of(topInset),
      LayoutConstraint.of(-topInset),
      LayoutConstraint.of(leftInset),
      LayoutConstraint.of(-leftInset)
    };
  }

  private static LayoutConstraint[] computeAbsoluteInsets(
    LayoutContext layoutContext, 
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint,
    ElementBox childBox
  ) {
    ActiveStyles styles = childBox.activeStyles();
    LayoutConstraint topInset = SizingUtil.evaluateBaseSize(
      layoutContext, parentHeightConstraint, styles.getProperty(CSSProperty.TOP));
    
      
    return new LayoutConstraint[] {
      LayoutConstraint.of(50),
      LayoutConstraint.of(50),
      LayoutConstraint.of(50),
      LayoutConstraint.of(50)
    };
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
