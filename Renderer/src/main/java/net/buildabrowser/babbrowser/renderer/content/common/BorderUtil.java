package net.buildabrowser.babbrowser.renderer.content.common;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderStyleValue;
import net.buildabrowser.babbrowser.renderer.box.EBDimensionsUtil;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class BorderUtil {
  
  private BorderUtil() {}

  public static void computeBorder(ElementBox childBox) {
    PropertyContainer properties = childBox.properties();
    float topBorder = computeBorder(childBox,
      properties.get(CSSProperty.BORDER_TOP_WIDTH), properties.get(CSSProperty.BORDER_TOP_STYLE));
    float bottomBorder = computeBorder(childBox,
      properties.get(CSSProperty.BORDER_BOTTOM_WIDTH), properties.get(CSSProperty.BORDER_BOTTOM_STYLE));
    float leftBorder = computeBorder(childBox,
      properties.get(CSSProperty.BORDER_LEFT_WIDTH), properties.get(CSSProperty.BORDER_LEFT_STYLE));
    float rightBorder = computeBorder(childBox,
      properties.get(CSSProperty.BORDER_RIGHT_WIDTH), properties.get(CSSProperty.BORDER_RIGHT_STYLE));
    EBDimensionsUtil.setComputedBorder(childBox, topBorder, bottomBorder, leftBorder, rightBorder);
  }

  public static float computeBorder(
    ElementBox childBox,
    CSSValue property,
    CSSValue styleProperty
  ) {
    if (styleProperty.equals(CSSValue.NONE) || styleProperty.equals(BorderStyleValue.HIDDEN)) {
      return 0;
    }
    
    LayoutConstraint constraint = SizingUtil.evaluateBaseSize(childBox.layoutContext(), LayoutConstraint.AUTO, property);
    return constraint.isPreLayoutConstraint() ? 0 : Math.max(0, constraint.value());
  }

}
