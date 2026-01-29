package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.floats.FloatValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public final class FloatLayout {
  
  private FloatLayout() {}

  public static UnmanagedBoxFragment renderFloat(
    LayoutContext layoutContext,
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    // TODO: Check height calculation
    LayoutConstraint childWidthConstraint = childBox.isReplaced() ?
      FlowWidthUtil.determineBlockReplacedWidthAndMargins(
        layoutContext, parentWidthConstraint, childBox) :
      FlowWidthUtil.determineFloatNonReplacedWidthAndMargins(
        layoutContext, parentWidthConstraint, childBox);
    LayoutConstraint childHeightContraint = childBox.isReplaced() ?
      FlowHeightUtil.evaluateReplacedBlockHeightAndMargins(
        layoutContext, parentHeightConstraint, parentWidthConstraint,
        childWidthConstraint, childBox) :
      FlowHeightUtil.evaluateNonReplacedBlockHeightAndMargins(
        layoutContext, parentHeightConstraint, parentWidthConstraint, childBox);

    if (!parentWidthConstraint.isPreLayoutConstraint()) {
      return childBox.content().layout(layoutContext, childWidthConstraint, childHeightContraint);
    }

    return new UnmanagedBoxFragment(0, 0, childBox, null);
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

}
