package net.buildabrowser.babbrowser.renderer.content.common.position;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.renderer.box.EBDimensionsUtil;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.content.common.MarginUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingHeightUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingWidthUtil;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;

public final class AbsolutePositionUtil {
  
  private AbsolutePositionUtil() {}

  // TODO: Handle replaced elements
  public static float[] computeAbsoluteInsets(
    ElementBox box, float refWidth, float refHeight,
    float[] parentBorder
  ) {
    MarginUtil.computeSimpleMargin(box, LayoutConstraint.of(refWidth));

    float[] insets = new float[6];
    computeHorizontalInsets(box, refWidth, insets, parentBorder);
    computeVerticalInsets(box, refHeight, insets, parentBorder);

    return insets;
  }

  private static void computeHorizontalInsets(
    ElementBox box, float parentWidth, float[] constraints,
    float[] parentBorder
  ) {
    ElementBoxDimensions dimensions = box.dimensions();
    PropertyContainer properties = box.properties();
    LayoutContext layoutContext = box.layoutContext();
    LayoutConstraint parentWidthConstraint = LayoutConstraint.of(parentWidth);
    LayoutConstraint left = SizingUtil.evaluateBaseSize(
      layoutContext, parentWidthConstraint, properties.get(CSSProperty.LEFT));
    LayoutConstraint right = SizingUtil.evaluateBaseSize(
      layoutContext, parentWidthConstraint, properties.get(CSSProperty.RIGHT));
    LayoutConstraint width = SizingWidthUtil.evaluateWidthSize(
      parentWidthConstraint, box);
    float staticPos = dimensions.staticX() - parentBorder[2];

    boolean isAllAuto =
      !left.isBounded()
      && !right.isBounded()
      && !width.isBounded();
    // TODO: Compute margins, respect text direction
    if (isAllAuto) {
      left = LayoutConstraint.of(staticPos);
    }

    boolean isNoneAuto =
      left.isBounded()
      && right.isBounded()
      && width.isBounded();
    // TODO: Compute margins, respect text direction
    if (isNoneAuto) {
      right = LayoutConstraint.AUTO;
    }

    boolean isLeftAuto = !left.isBounded();
    boolean isRightAuto = !right.isBounded();
    boolean isWidthAuto = !width.isBounded();

    float refWidth = parentWidth - dimensions.decorWidth();
    if (isLeftAuto && isWidthAuto && !isRightAuto) {
      float availableWidth = refWidth - right.value();
      width = shrinkToFit(box, refWidth, availableWidth);
      left = LayoutConstraint.of(refWidth - right.value() - width.value());
    } else if (isLeftAuto && isRightAuto && !isWidthAuto) {
      // TODO: Respect text direction
      left = LayoutConstraint.of(staticPos);
      right = LayoutConstraint.of(refWidth - left.value() - width.value());
    } else if (isWidthAuto && isRightAuto && !isLeftAuto) {
      float availableWidth = refWidth - left.value();
      width = shrinkToFit(box, refWidth, availableWidth);
      right = LayoutConstraint.of(refWidth - left.value() - width.value());
    } else if (isLeftAuto && !isWidthAuto && !isRightAuto) {
      left = LayoutConstraint.of(refWidth - width.value() - right.value());
    } else if (isWidthAuto && !isLeftAuto && !isRightAuto) {
      width = LayoutConstraint.of(refWidth - left.value() - right.value());
    } else if (isRightAuto && !isLeftAuto && !isWidthAuto) {
      right = LayoutConstraint.of(refWidth - width.value() - left.value());
    }

    assert left.isBounded();
    assert right.isBounded();
    assert width.isBounded();
    constraints[2] = left.value();
    constraints[3] = right.value();
    constraints[5] = width.value();
  }

  private static LayoutConstraint shrinkToFit(
    ElementBox box,
    float containingWidth,
    float availableWidth
  ) {
    float minWidth = EBDimensionsUtil.preferredMinWidthConstraint(box);
    float maxWidth = EBDimensionsUtil.preferredWidthConstraint(box);
    float usedWidth = Math.min(Math.max(minWidth, availableWidth), maxWidth);
    return SizingWidthUtil.clampWidth(
      LayoutConstraint.of(containingWidth), box, LayoutConstraint.of(usedWidth));
  }

  private static void computeVerticalInsets(
    ElementBox box, float parentHeight, float[] constraints,
    float[] parentBorder
  ) {
    ElementBoxDimensions dimensions = box.dimensions();
    PropertyContainer properties = box.properties();
    LayoutContext layoutContext = box.layoutContext();
    LayoutConstraint parentHeightConstraint = LayoutConstraint.of(parentHeight);
    LayoutConstraint top = SizingUtil.evaluateBaseSize(
      layoutContext, parentHeightConstraint, properties.get(CSSProperty.TOP));
    LayoutConstraint bottom = SizingUtil.evaluateBaseSize(
      layoutContext, parentHeightConstraint, properties.get(CSSProperty.BOTTOM));
    LayoutConstraint height = SizingHeightUtil.evaluateAdjustedHeightSize(
      parentHeightConstraint, box);
    float staticPos = dimensions.staticY() - parentBorder[0];

    boolean isAllAuto =
      !top.isBounded()
      && !bottom.isBounded()
      && !height.isBounded();
    // TODO: Compute margins
    if (isAllAuto) {
      top = LayoutConstraint.of(staticPos);
    }

    boolean isNoneAuto =
      top.isBounded()
      && bottom.isBounded()
      && height.isBounded();
    // TODO: Compute margins
    if (isNoneAuto) {
      bottom = LayoutConstraint.AUTO;
    }

    boolean isTopAuto = !top.isBounded();
    boolean isBottomAuto = !bottom.isBounded();
    boolean isWidthAuto = !height.isBounded();

    float refHeight = parentHeight - dimensions.decorHeight();
    if (isTopAuto && isWidthAuto && !isBottomAuto) {
      height = computeAutoHeight(box, parentHeight, constraints);
      top = LayoutConstraint.of(refHeight - bottom.value() - height.value());
    } else if (isTopAuto && isBottomAuto && !isWidthAuto) {
      top = LayoutConstraint.of(staticPos);
      bottom = LayoutConstraint.of(refHeight - top.value() - height.value());
    } else if (isWidthAuto && isBottomAuto && !isTopAuto) {
      height = computeAutoHeight(box, parentHeight, constraints);
      bottom = LayoutConstraint.of(refHeight - top.value() - height.value());
    } else if (isTopAuto && !isWidthAuto && !isBottomAuto) {
      top = LayoutConstraint.of(refHeight - height.value() - bottom.value());
    } else if (isWidthAuto && !isTopAuto && !isBottomAuto) {
      height = LayoutConstraint.of(refHeight - top.value() - bottom.value());
    } else if (isBottomAuto && !isTopAuto && !isWidthAuto) {
      bottom = LayoutConstraint.of(refHeight - height.value() - top.value());
    }

    assert top.isBounded();
    assert bottom.isBounded();
    assert height.isBounded();
    constraints[0] = top.value();
    constraints[1] = bottom.value();
    constraints[4] = height.value();
  }

  // TODO: Proep
  private static LayoutConstraint computeAutoHeight(
    ElementBox box,
    float containingWidth,
    float[] constraints
  ) {
    float usedHeight = box.layout(
      LayoutConstraint.of(constraints[5]),
      LayoutConstraint.AUTO).height(Measurement.CONTENT);
    return SizingHeightUtil.clampHeight(
      LayoutConstraint.of(containingWidth), box, LayoutConstraint.of(usedHeight));
  }

}
