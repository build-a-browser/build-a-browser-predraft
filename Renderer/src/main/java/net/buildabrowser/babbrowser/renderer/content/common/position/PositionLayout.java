package net.buildabrowser.babbrowser.renderer.content.common.position;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.mathClamp;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.renderer.box.EBDimensionsUtil;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.MarginUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingHeightUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingWidthUtil;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public final class PositionLayout {
  
  private PositionLayout() {}

  public static PosRefBoxFragment layout(
    ElementBox box
  ) {
    PosRefBoxFragment refFragment = new PosRefBoxFragment(box);
    box.updatePositioningFragment(refFragment);
    return refFragment;
  }

  public static UnmanagedBoxFragment<?> actuallyLayoutAbsolute(
    ElementBox refBox,
    float refWidth, float refHeight,
    float[] insets
  ) {
    float containingWidth = refWidth - insets[2] - insets[3];
    float containingHeight = refHeight - insets[0] - insets[1];

    LayoutConstraint baseWidth = SizingWidthUtil.evaluateAdjustedWidthSize(
      LayoutConstraint.of(containingWidth), refBox);
    LayoutConstraint baseHeight = SizingHeightUtil.evaluateAdjustedHeightSize(
      LayoutConstraint.of(containingHeight), refBox);

    // TODO: Handle sizes other than fit-content
    // TODO: Also clamp to max width and min width
    float fitContentWidth = mathClamp(
      containingWidth,
      EBDimensionsUtil.preferredMinWidthConstraint(refBox),
      EBDimensionsUtil.preferredWidthConstraint(refBox));
    float usedWidth = LayoutUtil.constraintOrDim(baseWidth, fitContentWidth);
    LayoutConstraint usedWidthConstraint = SizingHeightUtil.clampHeight(
      LayoutConstraint.of(containingWidth), refBox, LayoutConstraint.of(usedWidth));
    
    // TODO: Actually determine a height to use
    LayoutConstraint usedHeightConstraint = SizingHeightUtil.clampHeight(
      LayoutConstraint.of(containingHeight), refBox, baseHeight);

    UnmanagedBoxFragment<?> itemFragment = refBox.layout(
      usedWidthConstraint, usedHeightConstraint);

    MarginUtil.computeSimpleMargin(refBox, usedWidthConstraint);
    float[] margins = refBox.dimensions().getComputedMargin();
    itemFragment.setPos(margins[2], margins[0]);

    // TODO: Compute margins

    return itemFragment;
  }

  public static float[] positionAbsolute(
    float[] insets,
    UnmanagedBoxFragment<?> computedFragment,
    float refWidth, float refHeight,
    float[] parentBorder
  ) {
    PropertyContainer properties = computedFragment.box().properties();
    boolean topInsetIsAuto = properties.get(CSSProperty.TOP).equals(CSSValue.AUTO);
    boolean bottomInsetIsAuto = properties.get(CSSProperty.BOTTOM).equals(CSSValue.AUTO);
    boolean leftInsetIsAuto = properties.get(CSSProperty.LEFT).equals(CSSValue.AUTO);
    boolean rightInsetIsAuto = properties.get(CSSProperty.RIGHT).equals(CSSValue.AUTO);

    float leftPos = positionAbsoluteAxis(
      leftInsetIsAuto, rightInsetIsAuto, insets, 2,
      computedFragment.box().dimensions().staticX() - parentBorder[2],
      computedFragment.width(Measurement.BORDER), refWidth);
    float topPos = positionAbsoluteAxis(
      topInsetIsAuto, bottomInsetIsAuto, insets, 0,
      computedFragment.box().dimensions().staticY() - parentBorder[0],
      computedFragment.height(Measurement.BORDER), refHeight);

    float[] margin = computedFragment.box().dimensions().getComputedMargin();
    return new float[] {
      leftPos + margin[2],
      topPos + margin[0]
    };
  }

  private static float positionAbsoluteAxis(
    boolean topInsetIsAuto,
    boolean bottomInsetIsAuto,
    float[] insets,
    int conIndex,
    float staticPos,
    float itemSize,
    float axisSize
  ) {
    if (bottomInsetIsAuto && topInsetIsAuto) {
      return staticPos;
    } else if (bottomInsetIsAuto) {
      // TODO: Account for writing mode
      return insets[conIndex];
    } else if (topInsetIsAuto) {
      return axisSize - insets[conIndex + 1] - itemSize;
    } else {
      // TODO: Compute based on margins (for now, we'll use stronger inset)
      return insets[conIndex];
    }
  }

}
