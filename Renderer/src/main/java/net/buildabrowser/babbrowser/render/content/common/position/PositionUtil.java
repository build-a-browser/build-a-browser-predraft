package net.buildabrowser.babbrowser.render.content.common.position;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.render.layout.LayoutContext;

public final class PositionUtil {

  public static boolean affectsLayout(ElementBox box) {
    CSSValue position = box.activeStyles().getProperty(CSSProperty.POSITION);
    return position.equals(PositionValue.STATIC) || position.equals(PositionValue.RELATIVE) || position.equals(PositionValue.STICKY);
  }

  public static boolean affectsLayout(LayoutFragment fragment) {
    return
      !(fragment instanceof PosRefBoxFragment refFrag)
      || affectsLayout(refFrag.box());
  }

  public static float[] computeRelativeInsets(
    float parentWidth, float parentHeight, ElementBox childBox
  ) {
    ActiveStyles styles = childBox.activeStyles();
    float topInset = computeRelativeInset(
      styles.getProperty(CSSProperty.TOP), styles.getProperty(CSSProperty.BOTTOM),
      childBox, LayoutConstraint.of(parentHeight));
    float leftInset = computeRelativeInset(
      styles.getProperty(CSSProperty.LEFT), styles.getProperty(CSSProperty.RIGHT),
      childBox, LayoutConstraint.of(parentWidth));
    
    return new float[] {
      topInset, -topInset, leftInset, -leftInset
    };
  }

  private static float computeRelativeInset(
    CSSValue startProperty,
    CSSValue endProperty,
    ElementBox childBox,
    LayoutConstraint referenceConstraint
  ) {
    LayoutContext layoutContext = childBox.layoutContext();
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

  // TODO: Respect self-alignment
  public static float[] computeAbsoluteInsets(
    ElementBox box, float refWidth, float refHeight
  ) {
    ActiveStyles styles = box.activeStyles();
    LayoutContext layoutContext = box.layoutContext();
    LayoutConstraint refHeightConstraint = LayoutConstraint.of(refHeight);
    LayoutConstraint refWidthConstraint = LayoutConstraint.of(refWidth);
    LayoutConstraint topInset = SizingUtil.evaluateBaseSize(
      layoutContext, refHeightConstraint, styles.getProperty(CSSProperty.TOP));
    LayoutConstraint bottomInset = SizingUtil.evaluateBaseSize(
      layoutContext, refHeightConstraint, styles.getProperty(CSSProperty.BOTTOM));
    LayoutConstraint leftInset = SizingUtil.evaluateBaseSize(
      layoutContext, refWidthConstraint, styles.getProperty(CSSProperty.LEFT));
    LayoutConstraint rightInset = SizingUtil.evaluateBaseSize(
      layoutContext, refWidthConstraint, styles.getProperty(CSSProperty.RIGHT));
    
    
    LayoutConstraint[] initConstraints = new LayoutConstraint[] {
      topInset, bottomInset, leftInset, rightInset
    };
    float[] adjustedConstraints = new float[4];

    ElementBoxDimensions boxDimensions = box.dimensions();
    adjustAbsoluteConstraints(adjustedConstraints, initConstraints, 2, boxDimensions.staticX());
    adjustAbsoluteConstraints(adjustedConstraints, initConstraints, 0, boxDimensions.staticY());
      
    return adjustedConstraints;
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
