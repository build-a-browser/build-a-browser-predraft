package net.buildabrowser.babbrowser.browser.render.content.flexbox;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public final class FlexUtil {
  
  private FlexUtil() {}

  public static LayoutConstraint evaluateFlexBasis(
    ElementBox box,
    LayoutConstraint parentMainSize,
    CSSValue flexBasis,
    boolean isVertical
  ) {
    if (isVertical) {
      return SizingUtil.evaluateAdjustedHeightSize(
        parentMainSize, box, flexBasis);
    } else {
      return SizingUtil.evaluateAdjustedWidthSize(
        parentMainSize, box, flexBasis);
    }
  }

  public static LayoutConstraint boxCrossSize(
    ElementBox box,
    LayoutConstraint parentCrossSize,
    boolean isVertical
  ) {
    if (isVertical) {
      return SizingUtil.evaluateAdjustedWidthSize(
        parentCrossSize, box,
        box.activeStyles().getProperty(CSSProperty.WIDTH));
    } else {
      return SizingUtil.evaluateAdjustedHeightSize(
        parentCrossSize, box,
        box.activeStyles().getProperty(CSSProperty.HEIGHT));
    }
  }
  
}
