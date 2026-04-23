package net.buildabrowser.babbrowser.render.content.common.position;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.render.content.common.SizingHeightUtil;
import net.buildabrowser.babbrowser.render.content.common.SizingWidthUtil;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.fragment.PosRefBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint.LayoutConstraintType;

public final class PositionLayout {
  
  private PositionLayout() {}

  public static PosRefBoxFragment layout(
    ElementBox box
  ) {
    PosRefBoxFragment refFragment = new PosRefBoxFragment(box);
    box.updatePositioningFragment(refFragment);
    return refFragment;
  }

  public static UnmanagedBoxFragment actuallyLayoutAbsolute(
    ElementBox refBox,
    float refWidth, float refHeight,
    float[] insets
  ) {
    ElementBoxDimensions dimensions = refBox.dimensions();

    float containingWidth = refWidth - insets[2] - insets[3];
    float containingHeight = refHeight - insets[0] - insets[1];

    LayoutConstraint baseWidth = SizingWidthUtil.evaluateAdjustedWidthSize(
      LayoutConstraint.of(containingWidth), refBox);
    LayoutConstraint baseHeight = SizingHeightUtil.evaluateAdjustedHeightSize(
      LayoutConstraint.of(containingHeight), refBox);

    // TODO: Handle sizes other than fit-content
    // TODO: Also clamp to max width and min width
    float fitContentWidth = Math.clamp(
      containingWidth,
      dimensions.preferredMinWidthConstraint(),
      dimensions.preferredWidthConstraint());
    float usedWidth = baseWidth.type().equals(LayoutConstraintType.AUTO) ?
      fitContentWidth :
      baseWidth.value();
    LayoutConstraint usedWidthConstraint = SizingHeightUtil.clampHeight(
      LayoutConstraint.of(containingWidth), refBox, LayoutConstraint.of(usedWidth));
    
    // TODO: Actually determine a height to use
    LayoutConstraint usedHeightConstraint = SizingHeightUtil.clampHeight(
      LayoutConstraint.of(containingHeight), refBox, baseHeight);

    UnmanagedBoxFragment itemFragment = refBox.layout(
      usedWidthConstraint, usedHeightConstraint);
    itemFragment.setPos(0, 0);

    // TODO: Compute margins

    return itemFragment;
  }

  public static float[] positionAbsolute(
    float[] insets,
    UnmanagedBoxFragment computedFragment,
    float refWidth, float refHeight
  ) {
    ActiveStyles styles = computedFragment.box().activeStyles();
    boolean topInsetIsAuto = styles.getProperty(CSSProperty.TOP).equals(CSSValue.AUTO);
    boolean bottomInsetIsAuto = styles.getProperty(CSSProperty.BOTTOM).equals(CSSValue.AUTO);
    boolean leftInsetIsAuto = styles.getProperty(CSSProperty.LEFT).equals(CSSValue.AUTO);
    boolean rightInsetIsAuto = styles.getProperty(CSSProperty.RIGHT).equals(CSSValue.AUTO);

    float leftPos = positionAbsoluteAxis(
      leftInsetIsAuto, rightInsetIsAuto, insets, 2,
      computedFragment.width(Measurement.BORDER), refWidth);
    float topPos = positionAbsoluteAxis(
      topInsetIsAuto, bottomInsetIsAuto, insets, 0,
      computedFragment.height(Measurement.BORDER), refHeight);

    return new float[] { leftPos, topPos };
  }

  private static float positionAbsoluteAxis(
    boolean topInsetIsAuto,
    boolean bottomInsetIsAuto,
    float[] insets,
    int conIndex,
    float itemSize,
    float axisSize
  ) {
    if (bottomInsetIsAuto) {
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
