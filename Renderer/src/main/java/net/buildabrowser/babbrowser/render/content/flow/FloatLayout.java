package net.buildabrowser.babbrowser.render.content.flow;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.floats.FloatValue;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public final class FloatLayout {
  
  private FloatLayout() {}

  public static UnmanagedBoxFragment renderFloat(
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    // TODO: Check height calculation
    LayoutConstraint childWidthConstraint = childBox.isReplaced() ?
      FlowWidthUtil.determineBlockReplacedWidthAndMargins(
        parentWidthConstraint, childBox) :
      FlowWidthUtil.determineFloatNonReplacedWidthAndMargins(
        parentWidthConstraint, childBox);
    LayoutConstraint childHeightContraint = childBox.isReplaced() ?
      FlowHeightUtil.evaluateReplacedBlockHeightAndMargins(
        parentHeightConstraint, parentWidthConstraint,
        childWidthConstraint, childBox) :
      FlowHeightUtil.evaluateNonReplacedBlockHeightAndMargins(
        parentHeightConstraint, parentWidthConstraint, childBox);

    if (!parentWidthConstraint.isPreLayoutConstraint()) {
      return childBox.layout(childWidthConstraint, childHeightContraint);
    }

    return new UnmanagedBoxFragment(0, 0, childBox, null);
  }

  public static boolean addFloat(
    FlowRootContent rootContent,
    UnmanagedBoxFragment floatFragment,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint,
    float reservedWidth
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
