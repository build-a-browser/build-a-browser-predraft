package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.floats.FloatValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class FloatLayout {
  
  private FloatLayout() {}

  public static UnmanagedBoxFragment<?> renderFloat(
    ElementBox childBox,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint
  ) {
    // TODO: Check height calculation
    LayoutConstraint childWidthConstraint = childBox.isReplaced() ?
      FlowWidthUtil.determineBlockReplacedWidthAndMargins(
        parentWidthConstraint, parentHeightConstraint, childBox) :
      FlowWidthUtil.determineFloatNonReplacedWidthAndMargins(
        parentWidthConstraint, childBox);
    LayoutConstraint childHeightContraint = childBox.isReplaced() ?
      FlowHeightUtil.evaluateReplacedBlockHeightAndMargins(
        parentHeightConstraint, parentWidthConstraint,
        childWidthConstraint, childBox) :
      FlowHeightUtil.evaluateNonReplacedBlockHeightAndMargins(
        parentHeightConstraint, parentWidthConstraint, childBox);

    return childBox.layout(childWidthConstraint, childHeightContraint);
  }

  public static boolean addFloat(
    FlowContext flowContext,
    UnmanagedBoxFragment<?> floatFragment,
    LayoutConstraint parentWidthConstraint,
    LayoutConstraint parentHeightConstraint,
    float reservedWidth
  ) {
    FloatTracker floatTracker = flowContext.floatTracker();
    PropertyContainer properties = floatFragment.box().properties();

    return switch (properties.get(CSSProperty.FLOAT)) {
      case FloatValue.LEFT -> floatTracker.addLineStartFloat(floatFragment, parentWidthConstraint, reservedWidth);
      case FloatValue.RIGHT -> floatTracker.addLineEndFloat(floatFragment, parentWidthConstraint, reservedWidth);
      default -> throw new UnsupportedOperationException("Unrecognized float type!");
    };
  }

}
