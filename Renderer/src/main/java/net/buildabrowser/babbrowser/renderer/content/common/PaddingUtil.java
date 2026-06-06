package net.buildabrowser.babbrowser.renderer.content.common;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.renderer.box.EBDimensionsUtil;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class PaddingUtil {
  
  private PaddingUtil() {}

  public static void computePadding(
    ElementBox childBox, LayoutConstraint referenceConstraint
  ) {
    PropertyContainer properties = childBox.properties();
    float topPadding = computePadding(properties.get(CSSProperty.PADDING_TOP), childBox, referenceConstraint);
    float bottomPadding = computePadding(properties.get(CSSProperty.PADDING_BOTTOM), childBox, referenceConstraint);
    float leftPadding = computePadding(properties.get(CSSProperty.PADDING_LEFT), childBox, referenceConstraint);
    float rightPadding = computePadding(properties.get(CSSProperty.PADDING_RIGHT), childBox, referenceConstraint);
    EBDimensionsUtil.setComputedPadding(childBox, topPadding, bottomPadding, leftPadding, rightPadding);
  }

  private static float computePadding(
    CSSValue property, ElementBox childBox, LayoutConstraint referenceConstraint
  ) {
    LayoutConstraint constraint = SizingUtil.evaluateBaseSize(childBox.layoutContext(), referenceConstraint, property);
    return constraint.isPreLayoutConstraint() ? 0 : constraint.value();
  }

}
