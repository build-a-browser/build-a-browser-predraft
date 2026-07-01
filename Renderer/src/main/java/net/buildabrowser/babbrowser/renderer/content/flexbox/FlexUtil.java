package net.buildabrowser.babbrowser.renderer.content.flexbox;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignItemsValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.SizingHeightUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingWidthUtil;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

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
    ElementBox rootBox,
    ElementBox box,
    LayoutConstraint parentCrossSize,
    boolean isVertical
  ) {
    CSSValue alignItemsValue = FlexCrossSizeDetermination.getItemAlignment(rootBox, box);
    if (isVertical) {
      LayoutConstraint crossSize = SizingWidthUtil.evaluateAdjustedWidthSize(parentCrossSize, box);
      crossSize = chooseCrossSize(alignItemsValue, crossSize, parentCrossSize);
      return SizingWidthUtil.clampWidth(parentCrossSize, box, crossSize);
    } else {
      LayoutConstraint crossSize = SizingHeightUtil.evaluateAdjustedHeightSize(parentCrossSize, box);
      crossSize = chooseCrossSize(alignItemsValue, crossSize, parentCrossSize);
      return SizingHeightUtil.clampHeight(parentCrossSize, box, crossSize);
    }
  }

  private static LayoutConstraint chooseCrossSize(
    CSSValue alignItemsValue,
    LayoutConstraint crossSize,
    LayoutConstraint parentCrossSize
  ) {
    if (
      alignItemsValue.equals(AlignItemsValue.STRETCH)
      && !crossSize.isBounded()
      && parentCrossSize.isBounded()
    ) {
      return parentCrossSize;
    }

    return crossSize;
  }
  
}
