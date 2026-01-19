package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint.LayoutConstraintType;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutUtil;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.floats.FloatValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public final class FloatLayout {
  
  private FloatLayout() {}

  public static UnmanagedBoxFragment renderFloat(
    LayoutContext layoutContext,
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint,
    BlockFormattingContext referenceContext
  ) {
    FlowPaddingUtil.computePadding(layoutContext, childBox, referenceContext);

    // TODO: Check height calculation
    ActiveStyles childStyles = childBox.activeStyles();
    LayoutConstraint childWidthConstraint = childBox.isReplaced() ?
      FlowWidthUtil.determineBlockReplacedWidth(
        layoutContext, parentWidthConstraint, childStyles, childBox.dimensions()) :
      determineNonReplacedWidth(
        layoutContext, parentWidthConstraint, childStyles, childBox.dimensions());
    LayoutConstraint childHeightContraint = childBox.isReplaced() ?
      FlowHeightUtil.evaluateReplacedBlockHeight(
        layoutContext, parentHeightConstraint, childWidthConstraint,
        childStyles, childBox.dimensions()) :
      FlowHeightUtil.evaluateNonReplacedBlockLevelHeight(
        layoutContext, parentHeightConstraint, childStyles);

    if (!parentWidthConstraint.isPreLayoutConstraint()) {
      childBox.content().layout(layoutContext, childWidthConstraint, childHeightContraint);
    }

    int width = LayoutUtil.constraintOrDim(childWidthConstraint, childBox.dimensions().getComputedWidth());
    int height = childBox.dimensions().getComputedHeight();

    return new UnmanagedBoxFragment(width, height, childBox);
  }

  public static boolean addFloat(
    FlowRootContent rootContent,
    UnmanagedBoxFragment floatFragment,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint,
    int reservedWidth
  ) {
    FloatTracker floatTracker = rootContent.floatTracker();
    ActiveStyles childStyles = floatFragment.box().activeStyles();

    return switch (childStyles.getProperty(CSSProperty.FLOAT)) {
      case FloatValue.LEFT -> floatTracker.addLineStartFloat(floatFragment, parentWidthConstraint, reservedWidth);
      case FloatValue.RIGHT -> floatTracker.addLineEndFloat(floatFragment, parentWidthConstraint, reservedWidth);
      default -> throw new UnsupportedOperationException("Unrecognized float type!");
    };
  }

  private static LayoutConstraint determineNonReplacedWidth(
    LayoutContext layoutContext,
    LayoutConstraint parentWidthConstraint,
    ActiveStyles childStyles,
    ElementBoxDimensions dimensions
  ) {
    if (parentWidthConstraint.isPreLayoutConstraint()) {
      return parentWidthConstraint;
    }

    LayoutConstraint baseWidth = FlowWidthUtil.evaluateBaseSize(
      layoutContext, parentWidthConstraint, childStyles.getProperty(CSSProperty.WIDTH));
    
    if (!baseWidth.type().equals(LayoutConstraintType.AUTO)) {
      return baseWidth;
    }

    return LayoutConstraint.of(Math.min(
      // TODO: Account for margins
      Math.max(dimensions.preferredMinWidthConstraint(), parentWidthConstraint.value()),
      dimensions.preferredWidthConstraint()));
  }

}
