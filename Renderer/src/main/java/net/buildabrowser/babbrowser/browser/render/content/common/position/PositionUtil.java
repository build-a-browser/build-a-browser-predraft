package net.buildabrowser.babbrowser.browser.render.content.common.position;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.position.PositionValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public final class PositionUtil {

  public static boolean affectsLayout(ElementBox box) {
    CSSValue position = box.activeStyles().getProperty(CSSProperty.POSITION);
    return position.equals(PositionValue.STATIC) || position.equals(PositionValue.RELATIVE);
  }

  public static boolean affectsLayout(LayoutFragment fragment) {
    return
      !(fragment instanceof PosRefBoxFragment refFrag)
      || affectsLayout(refFrag.box());
  }
  
  public static float[] computeInsets(
    PosRefBoxFragment refFragment,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    PositionValue position = (PositionValue) refFragment.referenceBox().activeStyles().getProperty(CSSProperty.POSITION);
    if (position.equals(PositionValue.RELATIVE)) {
      return computeRelativeInsets(
        refFragment.referenceLayoutContext(),
        parentWidthConstraint, parentHeightConstraint,
        refFragment.referenceBox());
    } else {
      return computeAbsoluteInsets(refFragment, parentWidthConstraint, parentHeightConstraint);
    }
  }

  private static float[] computeRelativeInsets(
    LayoutContext layoutContext, 
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint,
    ElementBox childBox
  ) {
    ActiveStyles styles = childBox.activeStyles();
    float topInset = computeRelativeInset(
      styles.getProperty(CSSProperty.TOP), styles.getProperty(CSSProperty.BOTTOM),
      layoutContext, childBox, parentHeightConstraint);
    float leftInset = computeRelativeInset(
      styles.getProperty(CSSProperty.LEFT), styles.getProperty(CSSProperty.RIGHT),
      layoutContext, childBox, parentWidthConstraint);
    
    return new float[] {
      topInset, -topInset, leftInset, -leftInset
    };
  }

  // TODO: Respect self-alignment
  private static float[] computeAbsoluteInsets(
    PosRefBoxFragment refFragment,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    ActiveStyles styles = refFragment.referenceBox().activeStyles();
    LayoutContext layoutContext = refFragment.referenceLayoutContext();
    LayoutConstraint topInset = SizingUtil.evaluateBaseSize(
      layoutContext, parentHeightConstraint, styles.getProperty(CSSProperty.TOP));
    LayoutConstraint bottomInset = SizingUtil.evaluateBaseSize(
      layoutContext, parentHeightConstraint, styles.getProperty(CSSProperty.BOTTOM));
    LayoutConstraint leftInset = SizingUtil.evaluateBaseSize(
      layoutContext, parentHeightConstraint, styles.getProperty(CSSProperty.LEFT));
    LayoutConstraint rightInset = SizingUtil.evaluateBaseSize(
      layoutContext, parentHeightConstraint, styles.getProperty(CSSProperty.RIGHT));
    
    
    LayoutConstraint[] initConstraints = new LayoutConstraint[] {
      topInset, bottomInset, leftInset, rightInset
    };
    float[] adjustedConstraints = new float[4];

    // Need to account for layoutPos being based on contentPos, but the box draws it's own borders
    float borderPaddingWidth = refFragment.contentX() - refFragment.borderX();
    float borderPaddingHeight = refFragment.contentY() - refFragment.borderY();
    adjustAbsoluteConstraints(adjustedConstraints, initConstraints, 2, refFragment.layerX() - borderPaddingWidth);
    adjustAbsoluteConstraints(adjustedConstraints, initConstraints, 0, refFragment.layerY() - borderPaddingHeight);
      
    return adjustedConstraints;
  }

  private static float computeRelativeInset(
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

  private static void adjustAbsoluteConstraints(
    float[] adjustedConstraints,
    LayoutConstraint[] initConstraints,
    int conIndex,
    float staticPos
  ) {
    boolean firstIsAuto = initConstraints[conIndex].type().equals(LayoutConstraintType.AUTO);
    boolean secondIsAuto = initConstraints[conIndex + 1].type().equals(LayoutConstraintType.AUTO);
    if (firstIsAuto && secondIsAuto) {
      adjustedConstraints[conIndex] = staticPos;
      adjustedConstraints[conIndex + 1] = 0;
    } else if (firstIsAuto) {
      adjustedConstraints[conIndex] = 0;
      adjustedConstraints[conIndex + 1] = initConstraints[conIndex + 1].value();
    } else if (secondIsAuto) {
      adjustedConstraints[conIndex] = initConstraints[conIndex].value();
      adjustedConstraints[conIndex + 1] = 0;
    } else {
      adjustedConstraints[conIndex] = initConstraints[conIndex].value();
      adjustedConstraints[conIndex + 1] = initConstraints[conIndex + 1].value();
    }
  }

}
