package net.buildabrowser.babbrowser.renderer.content.flexbox;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignContentValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignItemsValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexWrapValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class FlexCrossSizeDetermination {

  private FlexCrossSizeDetermination() {}

  public static void determineCrossSize(
    ElementBox rootBox, List<FlexLine> lines,
    LayoutConstraint containerCrossSize, boolean isVertical
  ) {
    for (FlexLine line: lines) {
      for (FlexItem item: line.items()) {
        layoutItem(rootBox, item, containerCrossSize, isVertical);
      }

      calculateLineCrossSize(rootBox, line, containerCrossSize);
    }

    handleStretch(rootBox, lines, containerCrossSize);

    // TODO: Handle align-content: stretch and visibility: collapse
    determineItemCrossSizes(rootBox, lines, containerCrossSize, isVertical);
  }

  public static CSSValue getItemAlignment(ElementBox rootBox, ElementBox itemBox) {
    CSSValue itemAlignmentValue = itemBox.properties().get(CSSProperty.ALIGN_SELF);
    if (itemAlignmentValue.equals(CSSValue.AUTO)) {
      itemAlignmentValue = rootBox.properties().get(CSSProperty.ALIGN_ITEMS);
    }
    assert itemAlignmentValue instanceof AlignItemsValue;
    return itemAlignmentValue;
  }

  private static void layoutItem(
    ElementBox rootBox,
    FlexItem item, LayoutConstraint containerCrossSize, boolean isVertical
  ) {
    LayoutConstraint itemMainConstraint = LayoutConstraint.of(item.mainSize());
    LayoutConstraint itemCrossConstraint = FlexUtil.boxCrossSize(
      rootBox, item.box(), containerCrossSize, isVertical);
    if (!itemCrossConstraint.isBounded()) {
      // TODO: Actually fit-content
      itemCrossConstraint = LayoutConstraint.AUTO;
    }

    UnmanagedBoxFragment<?> boxFragment = item.box().layout(
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
    boolean isSingleLine = rootBox.properties().get(CSSProperty.FLEX_WRAP).equals(FlexWrapValue.NOWRAP);
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

  private static void handleStretch(
    ElementBox rootBox,
    List<FlexLine> lines,
    LayoutConstraint containerCrossSize
  ) {
    if (!containerCrossSize.isBounded()) return;
    if (!rootBox.properties().get(CSSProperty.ALIGN_CONTENT)
      .equals(AlignContentValue.STRETCH)) return;
    float lineCrossSize = 0;
    for (FlexLine line: lines) {
      lineCrossSize += line.crossSize();
    }
    float crossDiff = containerCrossSize.value() - lineCrossSize;
    if (crossDiff <= 0) return;
    float increasePerLine = crossDiff / lines.size();
    for (FlexLine line: lines) {
      line.setCrossSize(line.crossSize() + increasePerLine);
    }
  }

  private static void determineItemCrossSizes(
    ElementBox rootBox, List<FlexLine> lines,
    LayoutConstraint containerCrossSize, boolean isVertical
  ) {
    AlignItemsValue alignItemsValue = (AlignItemsValue) rootBox.properties()
      .get(CSSProperty.ALIGN_ITEMS);
    for (FlexLine line: lines) {
      for (FlexItem item: line.items()) {
        determineItemCrossSize(
          rootBox, item, line, alignItemsValue,
          containerCrossSize, isVertical);
      }
    }
  }

  private static void determineItemCrossSize(
    ElementBox rootBox, FlexItem item, FlexLine itemLine,
    AlignItemsValue alignItemsValue,
    LayoutConstraint containerCrossSize, boolean isVertical
  ) {
    CSSValue itemAlignmentValue = FlexItemCrossAlignment.getItemAlignment(
      alignItemsValue, item);

    LayoutConstraint itemMainConstraint = LayoutConstraint.of(item.mainSize());
    LayoutConstraint itemCrossConstraint = FlexUtil.boxCrossSize(
      rootBox, item.box(), containerCrossSize, isVertical);

    if (
      itemAlignmentValue.equals(AlignItemsValue.STRETCH)
      && !itemCrossConstraint.isBounded()
      // TODO: Other checks
    ) {
      // TODO: Clamp
      if (
        FlexItemCrossAlignment.hasCrossAutoMargin(isVertical, item)
      ) return;

      ElementBoxDimensions dimensions = item.box().dimensions();
      float[] margin = dimensions.getComputedMargin();
      float decorSize = isVertical ?
        dimensions.decorHeight() + margin[0] + margin[1] :
        dimensions.decorWidth() + margin[2] + margin[3];
      item.setCrossSize(itemLine.crossSize());
      itemCrossConstraint = LayoutConstraint.of(
        Math.max(0, itemLine.crossSize() - decorSize));

      UnmanagedBoxFragment<?> boxFragment = item.box().layout(
        isVertical ? itemCrossConstraint : itemMainConstraint,
        isVertical ? itemMainConstraint : itemCrossConstraint);
      item.setFragment(boxFragment);
    } else {
      item.setCrossSize(item.hypotheticalCrossSize());
    }
  }

}
