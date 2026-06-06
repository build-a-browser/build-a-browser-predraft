package net.buildabrowser.babbrowser.renderer.content.common;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.renderer.box.EBDimensionsUtil;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class MarginUtil {
  
  private MarginUtil() {}

  public static void computeSimpleMargin(
    ElementBox childBox, LayoutConstraint referenceConstraint
  ) {
    PropertyContainer properties = childBox.properties();
    
    float topMargin = computeMargin(properties.get(CSSProperty.MARGIN_TOP), childBox, referenceConstraint);
    float bottomMargin = computeMargin(properties.get(CSSProperty.MARGIN_BOTTOM), childBox, referenceConstraint);
    EBDimensionsUtil.setComputedVerticalMargin(childBox, topMargin, bottomMargin);

    float leftMargin = computeMargin(properties.get(CSSProperty.MARGIN_LEFT), childBox, referenceConstraint);
    float rightMargin = computeMargin(properties.get(CSSProperty.MARGIN_RIGHT), childBox, referenceConstraint);
    EBDimensionsUtil.setComputedHorizontalMargin(childBox, leftMargin, rightMargin);
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
