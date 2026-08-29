package net.buildabrowser.babbrowser.renderer.content.flexbox;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.align.AlignContentValue;
import net.buildabrowser.babbrowser.cssbase.property.align.AlignItemsValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.content.common.SizingHeightUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingWidthUtil;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class FlexUtil {
  
  private FlexUtil() {}

  public static LayoutConstraint evaluateFlexBasis(
    ElementBox box,
    LayoutConstraint parentMainSize,
    CSSProperty refProperty,
    CSSValue flexBasis,
    boolean isVertical
  ) {
    if (isVertical) {
      return SizingHeightUtil.evaluateAdjustedHeightSize(
        parentMainSize, box, refProperty, flexBasis);
    } else {
      return SizingWidthUtil.evaluateWidthSize(
        parentMainSize, box, refProperty, flexBasis);
    }
  }

  public static LayoutConstraint boxCrossSize(
    ElementBox rootBox,
    ElementBox box,
    LayoutConstraint parentCrossSize,
    boolean isVertical
  ) {
    if (isVertical) {
      LayoutConstraint crossSize = SizingWidthUtil.evaluateWidthSize(parentCrossSize, box);
      crossSize = chooseCrossSize(rootBox, box, crossSize, parentCrossSize, isVertical);
      return SizingWidthUtil.clampWidth(parentCrossSize, box, crossSize);
    } else {
      LayoutConstraint crossSize = SizingHeightUtil.evaluateAdjustedHeightSize(parentCrossSize, box);
      crossSize = chooseCrossSize(rootBox, box, crossSize, parentCrossSize, isVertical);
      return SizingHeightUtil.clampHeight(parentCrossSize, box, crossSize);
    }
  }

  private static LayoutConstraint chooseCrossSize(
    ElementBox rootBox,
    ElementBox itemBox,
    LayoutConstraint crossSize,
    LayoutConstraint parentCrossSize,
    boolean isVertical
  ) {

    CSSValue alignItemsValue = FlexCrossSizeDetermination.getItemAlignment(rootBox, itemBox);
    CSSValue alignContentValue = rootBox.properties().get(CSSProperty.ALIGN_CONTENT);
    boolean isContentStretch =
      alignContentValue.equals(AlignContentValue.STRETCH) 
      || alignContentValue.equals(AlignContentValue.NORMAL);
    CSSProperty crossProperty = isVertical ? CSSProperty.WIDTH : CSSProperty.HEIGHT;
    if (
      alignItemsValue.equals(AlignItemsValue.STRETCH)
      && isContentStretch
      && itemBox.properties().get(crossProperty).equals(CSSValue.AUTO)
      && parentCrossSize.isBounded()
    ) {
      ElementBoxDimensions dimensions = itemBox.dimensions();
      float[] margin = dimensions.getComputedMargin();
      float decorSize = isVertical ?
        dimensions.decorWidth() + margin[2] + margin[3] :
        dimensions.decorHeight() + margin[0] + margin[1];
      return LayoutConstraint.of(
        Math.max(0, parentCrossSize.value() - decorSize));
    }

    return crossSize;
  }
  
}
