package net.buildabrowser.babbrowser.renderer.content.generic;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.align.AlignItemsValue;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericAlignContentAligner.CrossAlignmentContext;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;

// align-items, align-self
public final class GenericAlignItemAligner {
  
  private GenericAlignItemAligner() {}

  public static void alignItems(
    CrossAlignmentContext alignmentContext, List<GenericTrack> lines
  ) {
    for (GenericTrack line: lines) {
      float edgeToBaseline = determineEdgeToBaseline(alignmentContext, line);
      for (GenericItem item: line.genericItems()) {
        alignItem(alignmentContext, line, item, edgeToBaseline);
      }
    }
  }

  public static AlignItemsValue getItemAlignment(
    AlignItemsValue alignItems, GenericItem item
  ) {
    CSSValue alignment = item.box().properties().get(CSSProperty.ALIGN_SELF);
    if (!alignment.equals(CSSValue.AUTO)) {
      return (AlignItemsValue) alignment;
    }
    return alignItems;
  }

  public static boolean hasCrossAutoMargin(
    boolean isVertical, GenericItem item
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
    CrossAlignmentContext alignmentContext, GenericTrack line
  ) {
    float maxEdgeToBaseline = 0;
    for (GenericItem item: line.genericItems()) {
      boolean hasCrossMargin = hasCrossAutoMargin(
        alignmentContext.isVertical(), item);
      if (hasCrossMargin) continue;
      AlignItemsValue itemAlignment = getItemAlignment(
        alignmentContext.alignItems(), item);
      if (
        !itemAlignment.equals(AlignItemsValue.BASELINE)
      ) continue;

      float itemCrossSize = item.crossSize();
      float itemBaseline = itemBaseline(alignmentContext, item);
      maxEdgeToBaseline = Math.max(maxEdgeToBaseline, itemCrossSize - itemBaseline);
    }

    return maxEdgeToBaseline;
  }

  private static void alignItem(
    CrossAlignmentContext alignmentContext,
    GenericTrack line,
    GenericItem item,
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
    // TODO: Edge cases for SELF_START, SELF_END
    float itemCrossPos = switch (itemAlignment) {
      case SELF_START, FLEX_START -> 0;
      case BASELINE -> edgeToBaseline - (itemCrossSize - itemBaseline(alignmentContext, item));
      case CENTER -> lineCrossSize / 2 - itemCrossSize / 2;
      case SELF_END, FLEX_END -> lineCrossSize - itemCrossSize;
      case STRETCH -> 0; // Stretch is handled in FlexCrossSizeDetermination
      default -> throw new UnsupportedOperationException(
        "Unrecognized item alignment: " + itemAlignment);
    };

    item.setCrossPos(itemCrossPos, alignmentContext.isVertical());
  }

  private static float itemBaseline(
    CrossAlignmentContext alignmentContext, GenericItem item
  ) {
    // TODO: Implement
    return alignmentContext.isVertical() ?
      0 : // TODO: Calculate baseline in vertical
      item.fragment().lastBaseline(Measurement.MARGIN);
  }

}
