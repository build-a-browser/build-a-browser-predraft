package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class FlowLinePositioner {
  
  private FlowLinePositioner() {}

  public static float computeFirstBaseline(
    LayoutFragment fragments,
    float height, float contentHeight
  ) {
    // TODO: Use the correct font metric
    assert IntrusiveList._ensureNoLoops(fragments);
    float baseline = (height - contentHeight) / 2;
    while (fragments != null) {
      float nextBaseline = fragments.firstBaseline(Measurement.MARGIN);
      baseline = Math.max(baseline, nextBaseline);
      fragments = fragments.next();
    }

    return baseline;
  }

  public static float computeLastBaseline(
    LayoutFragment fragments,
    float height, float contentHeight
  ) {
    // TODO: Use the correct font metric
    assert IntrusiveList._ensureNoLoops(fragments);
    float baseline = (height - contentHeight) / 2;
    while (fragments != null) {
      float nextBaseline = itemBaselineLast(fragments);
      baseline = Math.max(baseline, nextBaseline);
      fragments = fragments.next();
    }

    return baseline;
  }

  public static void positionLine(
    FlowContext flowContext,
    LineBoxFragment fragment,
    LayoutConstraint inlineConstraint,
    PropertyContainer lineProperties
  ) {
    positionFragmentElements(
      fragment, fragment.fragments(), inlineConstraint);
    float startPos = flowContext.floatTracker().lineStartPos();
    float inlineOffset = inlineConstraint.isBounded() ?
      FlowAlignUtil.alignFragment(
        lineProperties, startPos,
        flowContext.floatTracker().lineEndPos(inlineConstraint),
        fragment.width(Measurement.CONTENT)) :
      startPos;
    flowContext.blockLayout().addFinishedFragment(
      fragment, inlineOffset, inlineConstraint);
  }

  public static float itemBaselineLast(LayoutFragment fragment) {
    float itemBaseline = fragment.lastBaseline(Measurement.BORDER);
    if (fragment instanceof BoxFragment boxFragment) {
      float[] margin = boxFragment.box().dimensions().getComputedMargin();
      itemBaseline = Math.max(0, itemBaseline + margin[1]);
    }

    return itemBaseline;
  }

  // TODO: Support other alignments
  private static void positionFragmentElements(
    LayoutFragment fragment,
    LayoutFragment fragments,
    LayoutConstraint relatedConstraint
  ) {
    float x = 0;
    float baselinePos = fragment.height(Measurement.CONTENT) - fragment.lastBaseline(Measurement.CONTENT);

    LayoutFragment nextChild = fragments;
    while (nextChild != null) {
      LayoutFragment child = nextChild;
      nextChild = nextChild.next();

      child.setPos(0, 0); // Cheat to disable unset X assertions for next line
      float marginX = child.posX(Measurement.BORDER) - child.posX(Measurement.MARGIN);
      float posY = baselinePos - child.height(Measurement.BORDER) + child.lastBaseline(Measurement.BORDER);
      child.setPos(x + marginX, posY);

      if (!PositionUtil.affectsLayout(child)) continue;
      x += child.width(Measurement.MARGIN);
      if (child instanceof ManagedBoxFragment managedBoxFragment) {
        positionFragmentElements(
          managedBoxFragment,
          managedBoxFragment.fragments(),
          relatedConstraint);
      }
    }
  }

}
