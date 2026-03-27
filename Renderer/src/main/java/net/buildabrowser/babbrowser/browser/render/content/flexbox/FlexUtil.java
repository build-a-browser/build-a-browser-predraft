package net.buildabrowser.babbrowser.browser.render.content.flexbox;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.SizingHeightUtil;
import net.buildabrowser.babbrowser.browser.render.content.common.SizingWidthUtil;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
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
      return SizingHeightUtil.evaluateAdjustedHeightSize(
        parentMainSize, box, flexBasis);
    } else {
      return SizingWidthUtil.evaluateAdjustedWidthSize(
        parentMainSize, box, flexBasis);
    }
  }

  public static LayoutConstraint boxCrossSize(
    ElementBox box,
    LayoutConstraint parentCrossSize,
    boolean isVertical
  ) {
    if (isVertical) {
      LayoutConstraint crossSize = SizingWidthUtil.evaluateAdjustedWidthSize(parentCrossSize, box);
      return SizingWidthUtil.clampWidth(parentCrossSize, box, crossSize);
    } else {
      LayoutConstraint crossSize = SizingHeightUtil.evaluateAdjustedHeightSize(parentCrossSize, box);
      return SizingHeightUtil.clampHeight(parentCrossSize, box, crossSize);
    }
  }
  
}
