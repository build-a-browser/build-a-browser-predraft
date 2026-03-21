package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;

public final class FlowHeightUtil {
  
  private FlowHeightUtil() {}

  // TODO: Handle the case where both width and height are auto and min/max constraints are present
  public static LayoutConstraint evaluateReplacedBlockHeightAndMargins(
    LayoutConstraint parentHeightConstraint,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint childWidthConstraint,
    ElementBox childBox
  ) {
    computeVerticalMarginsOrZero(childBox, parentWidthConstraint);

    if (parentHeightConstraint.isPreLayoutConstraint() || childWidthConstraint.isPreLayoutConstraint()) {
      return parentHeightConstraint;
    }

    LayoutConstraint determinedHeightConstraint = SizingUtil.evaluateAdjustedHeightSize(
      parentHeightConstraint, childBox);
    
    boolean isHeightAuto = determinedHeightConstraint.type().equals(LayoutConstraintType.AUTO);
    ElementBoxDimensions boxDimensions = childBox.dimensions();
    LayoutConstraint chosenConstraint = determinedHeightConstraint;
    if (
      childWidthConstraint.type().equals(LayoutConstraintType.AUTO)
      && isHeightAuto
      && boxDimensions.intrinsicHeight() != -1
    ) {
      chosenConstraint = LayoutConstraint.of(boxDimensions.intrinsicHeight());
    } else if (isHeightAuto && boxDimensions.intrinsicRatio() != -1) {
      chosenConstraint = LayoutConstraint.of((int) (childWidthConstraint.value() / boxDimensions.intrinsicRatio())); 
    } else if (isHeightAuto && boxDimensions.intrinsicHeight() != -1) {
      chosenConstraint = LayoutConstraint.of(boxDimensions.intrinsicHeight());
    } else if (isHeightAuto) {
      // TODO: Viewport width
      chosenConstraint = LayoutConstraint.of(Math.min(childWidthConstraint.value() / 2, 150));
    }

    return SizingUtil.clampHeight(parentHeightConstraint, childBox, chosenConstraint);
  }

  public static LayoutConstraint evaluateNonReplacedBlockHeightAndMargins(
    LayoutConstraint parentHeightConstraint,
    LayoutConstraint parentWidthConstraint,
    ElementBox childBox
  ) {
    computeVerticalMarginsOrZero(childBox, parentWidthConstraint);

    // TODO: An actual proper implementation
    LayoutConstraint determinedConstraint = SizingUtil.evaluateAdjustedHeightSize(
      parentHeightConstraint, childBox);

    return SizingUtil.clampHeight(parentHeightConstraint, childBox, determinedConstraint);
  }

  public static void computeVerticalMarginsOrZero(
    ElementBox childBox, LayoutConstraint parentWidthConstraint
  ) {
    ActiveStyles childStyles = childBox.activeStyles();
    LayoutConstraint marginTopConstraint = SizingUtil.evaluateBaseSize(
      childBox.layoutContext(), parentWidthConstraint, childStyles.getProperty(CSSProperty.MARGIN_TOP));
    LayoutConstraint marginBottomConstraint = SizingUtil.evaluateBaseSize(
      childBox.layoutContext(), parentWidthConstraint, childStyles.getProperty(CSSProperty.MARGIN_BOTTOM));

    boolean isTopMarginSet = marginTopConstraint.isBounded();
    boolean isBottomMarginSet = marginBottomConstraint.isBounded();
    float usedTopMargin = isTopMarginSet ? marginTopConstraint.value() : 0;
    float usedBottomMargin = isBottomMarginSet ? marginBottomConstraint.value() : 0;

    childBox.dimensions().setComputedVerticalMargin(usedTopMargin, usedBottomMargin);
  }

}
