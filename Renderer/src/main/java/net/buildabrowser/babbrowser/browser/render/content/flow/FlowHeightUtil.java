package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public final class FlowHeightUtil {
  
  private FlowHeightUtil() {}

  public static LayoutConstraint evaluateReplacedBlockHeightAndMargins(
    LayoutContext layoutContext,
    LayoutConstraint parentHeightConstraint,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint childWidthConstraint,
    ElementBox childBox
  ) {
    computeVerticalMarginsOrZero(layoutContext, childBox, parentWidthConstraint);

    if (parentHeightConstraint.isPreLayoutConstraint() || childWidthConstraint.isPreLayoutConstraint()) {
      return parentHeightConstraint;
    }

    LayoutConstraint determinedHeightConstraint = SizingUtil.evaluateBaseSize(
      layoutContext, parentHeightConstraint, childBox.activeStyles().getProperty(CSSProperty.HEIGHT));
    
    boolean isHeightAuto = determinedHeightConstraint.type().equals(LayoutConstraintType.AUTO);
    ElementBoxDimensions boxDimensions = childBox.dimensions();
    if (
      childWidthConstraint.type().equals(LayoutConstraintType.AUTO)
      && isHeightAuto
      && boxDimensions.intrinsicHeight() != -1
    ) {
      return LayoutConstraint.of(boxDimensions.intrinsicHeight());
    } else if (isHeightAuto && boxDimensions.intrinsicRatio() != -1) {
      return LayoutConstraint.of((int) (childWidthConstraint.value() / boxDimensions.intrinsicRatio())); 
    } else if (isHeightAuto && boxDimensions.intrinsicHeight() != -1) {
      return LayoutConstraint.of(boxDimensions.intrinsicHeight());
    } else if (isHeightAuto) {
      // TODO: Viewport width
      return LayoutConstraint.of(Math.min(childWidthConstraint.value() / 2, 150));
    } else {
      return determinedHeightConstraint;
    }
  }

  public static LayoutConstraint evaluateNonReplacedBlockHeightAndMargins(
    LayoutContext layoutContext,
    LayoutConstraint parentHeightConstraint,
    LayoutConstraint parentWidthConstraint,
    ElementBox childBox
  ) {
    computeVerticalMarginsOrZero(layoutContext, childBox, parentWidthConstraint);

    // TODO: An actual proper implementation
    ActiveStyles childStyles = childBox.activeStyles();
    LayoutConstraint determinedConstraint = SizingUtil.evaluateBaseSize(
      layoutContext, parentHeightConstraint, childStyles.getProperty(CSSProperty.HEIGHT));

    return determinedConstraint;
  }

  public static void computeVerticalMarginsOrZero(
    LayoutContext layoutContext,
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint
  ) {
    ActiveStyles childStyles = childBox.activeStyles();
    LayoutConstraint marginTopConstraint = SizingUtil.evaluateBaseSize(
      layoutContext, parentWidthConstraint, childStyles.getProperty(CSSProperty.MARGIN_TOP));
    LayoutConstraint marginBottomConstraint = SizingUtil.evaluateBaseSize(
      layoutContext, parentWidthConstraint, childStyles.getProperty(CSSProperty.MARGIN_BOTTOM));

    boolean isTopMarginSet = marginTopConstraint.type().equals(LayoutConstraintType.BOUNDED);
    boolean isBottomMarginSet = marginBottomConstraint.type().equals(LayoutConstraintType.BOUNDED);
    float usedTopMargin = isTopMarginSet ? marginTopConstraint.value() : 0;
    float usedBottomMargin = isBottomMarginSet ? marginBottomConstraint.value() : 0;

    childBox.dimensions().setComputedVerticalMargin(usedTopMargin, usedBottomMargin);
  }

}
