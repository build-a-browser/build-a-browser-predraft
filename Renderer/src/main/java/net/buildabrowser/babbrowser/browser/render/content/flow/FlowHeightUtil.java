package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public final class FlowHeightUtil {
  
  private FlowHeightUtil() {}

  public static LayoutConstraint evaluateReplacedBlockHeight(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    LayoutConstraint widthConstraint,
    ActiveStyles childStyles,
    ElementBoxDimensions dimensions
  ) {
    if (parentConstraint.isPreLayoutConstraint() || widthConstraint.isPreLayoutConstraint()) {
      return parentConstraint;
    }

    LayoutConstraint determinedWidthConstraint = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint, childStyles.getProperty(CSSProperty.WIDTH));
    LayoutConstraint determinedHeightConstraint = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint, childStyles.getProperty(CSSProperty.HEIGHT));
    
    boolean isHeightAuto = determinedHeightConstraint.type().equals(LayoutConstraintType.AUTO);
    if (
      determinedWidthConstraint.type().equals(LayoutConstraintType.AUTO)
      && isHeightAuto
      && dimensions.intrinsicHeight() != -1
    ) {
      return LayoutConstraint.of(dimensions.intrinsicHeight());
    } else if (isHeightAuto && dimensions.intrinsicRatio() != -1) {
      return LayoutConstraint.of((int) (widthConstraint.value() / dimensions.intrinsicRatio())); 
    } else if (isHeightAuto && dimensions.intrinsicHeight() != -1) {
      return LayoutConstraint.of(dimensions.intrinsicHeight());
    } else if (isHeightAuto) {
      // TODO: Viewport width
      return LayoutConstraint.of(Math.min(determinedWidthConstraint.value() / 2, 150));
    } else {
      return determinedHeightConstraint;
    }
  }

  public static LayoutConstraint evaluateNonReplacedBlockLevelHeight(
    LayoutContext layoutContext,
    LayoutConstraint parentConstraint,
    ActiveStyles childStyles
  ) {
    // TODO: An actual proper implementation
    LayoutConstraint determinedConstraint = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentConstraint, childStyles.getProperty(CSSProperty.HEIGHT));

    return determinedConstraint;
  }

}
