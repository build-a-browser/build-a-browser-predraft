package net.buildabrowser.babbrowser.renderer.content.common.position;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public final class PositionUtil {

  public static boolean affectsLayout(ElementBox box) {
    CSSValue position = box.properties().get(CSSProperty.POSITION);
    return
      position.equals(PositionValue.STATIC)
      || position.equals(PositionValue.RELATIVE)
      || position.equals(PositionValue.STICKY);
  }

  public static boolean affectsLayout(LayoutFragment fragment) {
    return
      !(fragment instanceof PosRefBoxFragment refFrag)
      || affectsLayout(refFrag.box());
  }

  public static float[] computeRelativeInsets(
    float parentWidth, float parentHeight, ElementBox childBox
  ) {
    PropertyContainer properties = childBox.properties();
    float topInset = computeRelativeInset(
      properties.get(CSSProperty.TOP), properties.get(CSSProperty.BOTTOM),
      childBox, LayoutConstraint.of(parentHeight));
    float leftInset = computeRelativeInset(
      properties.get(CSSProperty.LEFT), properties.get(CSSProperty.RIGHT),
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

  public static float[] computeStickyInsets(
    ElementBox childBox, float parentWidth, float parentHeight
  ) {
    LayoutConstraint heightConstraint = LayoutConstraint.of(parentHeight);
    LayoutConstraint widthConstraint = LayoutConstraint.of(parentWidth);
    return new float[] {
      computeStickyInset(CSSProperty.TOP, childBox, heightConstraint),
      computeStickyInset(CSSProperty.BOTTOM, childBox, heightConstraint),
      computeStickyInset(CSSProperty.LEFT, childBox, widthConstraint),
      computeStickyInset(CSSProperty.RIGHT, childBox, widthConstraint)
    };
  }

  private static float computeStickyInset(
    CSSProperty property,
    ElementBox childBox,
    LayoutConstraint referenceConstraint
  ) {
    CSSValue propertyValue = childBox.properties().get(property);
    LayoutContext layoutContext = childBox.layoutContext();
    LayoutConstraint startConstraint = SizingUtil.evaluateBaseSize(
      layoutContext, referenceConstraint, propertyValue);
    return LayoutUtil.constraintOrDim(startConstraint, Float.NaN);
  }

  public static boolean isStaticX(ElementBox box) {
    PropertyContainer properties = box.properties();
    boolean leftInsetIsAuto = properties.get(CSSProperty.LEFT).equals(CSSValue.AUTO);
    boolean rightInsetIsAuto = properties.get(CSSProperty.RIGHT).equals(CSSValue.AUTO);
    return leftInsetIsAuto && rightInsetIsAuto;
  }
  
  public static boolean isStaticY(ElementBox box) {
    PropertyContainer properties = box.properties();
    boolean topInsetIsAuto = properties.get(CSSProperty.TOP).equals(CSSValue.AUTO);
    boolean bottomInsetIsAuto = properties.get(CSSProperty.BOTTOM).equals(CSSValue.AUTO);
    return topInsetIsAuto && bottomInsetIsAuto;
  }

}
