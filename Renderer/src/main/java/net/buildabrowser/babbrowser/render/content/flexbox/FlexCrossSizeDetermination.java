package net.buildabrowser.babbrowser.render.content.flexbox;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignItemsValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexWrapValue;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public final class FlexCrossSizeDetermination {

  private FlexCrossSizeDetermination() {}

  public static void determineCrossSize(
    ElementBox rootBox, List<FlexLine> lines,
    LayoutConstraint containerCrossSize, boolean isVertical
  ) {
    for (FlexLine line: lines) {
      for (FlexItem item: line.items()) {
        layoutItem(item, containerCrossSize, isVertical);
      }

      calculateLineCrossSize(rootBox, line, containerCrossSize);
    }

    // TODO: Handle align-content: stretch and visibility: collapse
    determineItemCrossSizes(rootBox, lines, containerCrossSize, isVertical);
  }

  private static void layoutItem(
    FlexItem item, LayoutConstraint containerCrossSize, boolean isVertical
  ) {
    LayoutConstraint itemMainConstraint = LayoutConstraint.of(item.mainSize());
    LayoutConstraint itemCrossConstraint = FlexUtil.boxCrossSize(item.box(), containerCrossSize, isVertical);
    if (!itemCrossConstraint.isBounded()) {
      // TODO: Actually fit-content
      itemCrossConstraint = LayoutConstraint.AUTO;
    }

    UnmanagedBoxFragment boxFragment = item.box().layout(
      isVertical ? itemCrossConstraint : itemMainConstraint,
      isVertical ? itemMainConstraint : itemCrossConstraint);
    item.setFragment(boxFragment);

    item.setHypotheticalCrossSize(isVertical ?
      boxFragment.width(Measurement.MARGIN) :
      boxFragment.height(Measurement.MARGIN));
  }

  private static void calculateLineCrossSize(
    ElementBox rootBox, FlexLine line, LayoutConstraint containerCrossSize
  ) {
    boolean isSingleLine = rootBox.activeStyles().getProperty(CSSProperty.FLEX_WRAP).equals(FlexWrapValue.NOWRAP);
    if (isSingleLine && containerCrossSize.isBounded()) {
      line.setCrossSize(containerCrossSize.value());
      return;
    }

    // TODO: Step 1

    float largestHypotheticalCrossSize = 0;
    for (FlexItem item: line.items()) {
      largestHypotheticalCrossSize = Math.max(
        largestHypotheticalCrossSize,
        item.hypotheticalCrossSize());
    }

    line.setCrossSize(largestHypotheticalCrossSize);

    // TODO: Clamp to min/max cross sizes
  }

  private static void determineItemCrossSizes(
    ElementBox rootBox, List<FlexLine> lines,
    LayoutConstraint containerCrossSize, boolean isVertical
  ) {
    for (FlexLine line: lines) {
      for (FlexItem item: line.items()) {
        determineItemCrossSize(rootBox, item, line, containerCrossSize, isVertical);
      }
    }
  }

  private static void determineItemCrossSize(
    ElementBox rootBox, FlexItem item, FlexLine itemLine,
    LayoutConstraint containerCrossSize, boolean isVertical
  ) {
    CSSValue itemAlignmentValue = item.box().activeStyles().getProperty(CSSProperty.ALIGN_SELF);
    if (itemAlignmentValue.equals(CSSValue.AUTO)) {
      itemAlignmentValue = rootBox.activeStyles().getProperty(CSSProperty.ALIGN_ITEMS);
    }
    assert itemAlignmentValue instanceof AlignItemsValue;

    LayoutConstraint itemMainConstraint = LayoutConstraint.of(item.mainSize());
    LayoutConstraint itemCrossConstraint =
      FlexUtil.boxCrossSize(item.box(), containerCrossSize, isVertical);

    if (
      itemAlignmentValue.equals(AlignItemsValue.STRETCH)
      && containerCrossSize.isBounded()
      && !itemCrossConstraint.isBounded()
      // TODO: Other checks
    ) {
      // TODO: Clamp
      // TODO: Need to account for margin
      item.setCrossSize(containerCrossSize.value());
      itemCrossConstraint = LayoutConstraint.of(itemLine.crossSize());

      UnmanagedBoxFragment boxFragment = item.box().layout(
        isVertical ? itemCrossConstraint : itemMainConstraint,
        isVertical ? itemMainConstraint : itemCrossConstraint);
      item.setFragment(boxFragment);
    } else {
      item.setCrossSize(item.hypotheticalCrossSize());
    }
  }

}
