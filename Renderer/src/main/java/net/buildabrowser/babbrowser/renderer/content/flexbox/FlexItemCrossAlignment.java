package net.buildabrowser.babbrowser.renderer.content.flexbox;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignItemsValue;
import net.buildabrowser.babbrowser.renderer.content.flexbox.FlexLineCrossAlignment.CrossAlignmentContext;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;

public final class FlexItemCrossAlignment {
  
  private FlexItemCrossAlignment() {}

  public static void alignItems(
    CrossAlignmentContext alignmentContext, List<FlexLine> lines
  ) {
    for (FlexLine line: lines) {
      float edgeToBaseline = determineEdgeToBaseline(alignmentContext, line);
      for (FlexItem item: line.items()) {
        alignItem(alignmentContext, line, item, edgeToBaseline);
      }
    }
  }

  public static AlignItemsValue getItemAlignment(
    AlignItemsValue alignItems, FlexItem item
  ) {
    CSSValue alignment = item.box().properties().get(CSSProperty.ALIGN_SELF);
    if (!alignment.equals(CSSValue.AUTO)) {
      return (AlignItemsValue) alignment;
    }
    return alignItems;
  }

  public static boolean hasCrossAutoMargin(
    boolean isVertical, FlexItem item
  ) {
    PropertyContainer properties = item.box().properties();
    if (isVertical) {
      return
        properties.get(CSSProperty.MARGIN_LEFT).equals(CSSValue.AUTO)
        || properties.get(CSSProperty.MARGIN_RIGHT).equals(CSSValue.AUTO);
    } else {
      return
        properties.get(CSSProperty.MARGIN_TOP).equals(CSSValue.AUTO)
        || properties.get(CSSProperty.MARGIN_BOTTOM).equals(CSSValue.AUTO);
    }
  }

  private static float determineEdgeToBaseline(
    CrossAlignmentContext alignmentContext, FlexLine line
  ) {
    float maxEdgeToBaseline = 0;
    for (FlexItem item: line.items()) {
      boolean hasCrossMargin = hasCrossAutoMargin(
        alignmentContext.isVertical(), item);
      if (hasCrossMargin) continue;
      AlignItemsValue itemAlignment = getItemAlignment(
        alignmentContext.alignItems(), item);
      if (
        !itemAlignment.equals(AlignItemsValue.BASELINE)
      ) continue;

      float itemCrossSize = item.usedCrossSize();
      float itemBaseline = itemBaseline(alignmentContext, item);
      maxEdgeToBaseline = Math.max(maxEdgeToBaseline, itemCrossSize - itemBaseline);
    }

    return maxEdgeToBaseline;
  }

  private static void alignItem(
    CrossAlignmentContext alignmentContext,
    FlexLine line,
    FlexItem item,
    float edgeToBaseline
  ) {
    boolean hasCrossMargin = hasCrossAutoMargin(
      alignmentContext.isVertical(), item);
    if (hasCrossMargin) return;
    AlignItemsValue itemAlignment = getItemAlignment(
      alignmentContext.alignItems(), item);
    float lineCrossSize = line.crossSize();
    float itemCrossSize = alignmentContext.isVertical() ?
      item.fragment().width(Measurement.MARGIN) :
      item.fragment().height(Measurement.MARGIN);
    float itemCrossPos = switch (itemAlignment) {
      case FLEX_START -> 0;
      case BASELINE -> edgeToBaseline - (itemCrossSize - itemBaseline(alignmentContext, item));
      case CENTER -> lineCrossSize / 2 - itemCrossSize / 2;
      case FLEX_END -> lineCrossSize - itemCrossSize;
      case STRETCH -> 0; // Stretch is handled in FlexCrossSizeDetermination
      default -> throw new UnsupportedOperationException(
        "Unrecognized item alignment: " + itemAlignment);
    };

    if (alignmentContext.isVertical()) {
      item.fragment().setPos(
        itemCrossPos,
        item.fragment().posY(Measurement.BORDER));
    } else {
      item.fragment().setPos(
        item.fragment().posX(Measurement.BORDER),
        itemCrossPos);
    }
  }

  private static float itemBaseline(
    CrossAlignmentContext alignmentContext, FlexItem item
  ) {
    // TODO: Implement
    return alignmentContext.isVertical() ?
      0 : // TODO: Calculate baseline in vertical
      item.fragment().lastBaseline(Measurement.MARGIN);
  }

}
