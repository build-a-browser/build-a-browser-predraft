package net.buildabrowser.babbrowser.renderer.content.flexbox;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.flex.AlignContentValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignItemsValue;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class FlexLineCrossAlignment {
  
  private FlexLineCrossAlignment() {}

  public static void alignLines(
    CrossAlignmentContext alignmentContext, List<FlexLine> lines
  ) {
    // TODO: But does the align affect overflow?
    float gapSize = alignmentContext.crossGap();
    float spaceLeft = computeRemainingFreeSpace(alignmentContext, lines);
    switch (alignmentContext.alignContent()) {
      case FLEX_START -> positionLinesAt(0, alignmentContext, lines, gapSize);
      case FLEX_END -> positionLinesAt(spaceLeft, alignmentContext, lines, gapSize);
      case CENTER -> positionLinesAt(spaceLeft / 2, alignmentContext, lines, gapSize);
      case SPACE_BETWEEN -> positionLinesBetween(alignmentContext, lines, spaceLeft);
      case SPACE_AROUND -> positionLinesAround(alignmentContext, lines, spaceLeft);
      case STRETCH -> positionLinesAt(0, alignmentContext, lines, gapSize); // TODO
      default -> throw new UnsupportedOperationException("Unsupported alignment!");
    }
  }

  private static void positionLinesAt(
    float startPos, CrossAlignmentContext alignmentContext, List<FlexLine> lines, float gapSize
  ) {
    for (FlexLine line: lines) {
      setCrossPos(line, startPos, alignmentContext.isVertical());
      startPos += line.crossSize();
      startPos += gapSize;
    }
  }

  private static void positionLinesBetween(
    CrossAlignmentContext alignmentContext, List<FlexLine> lines, float spaceLeft
  ) {
    float crossGap = alignmentContext.crossGap();
    if (spaceLeft < 0 || lines.size() == 1) {
      // TODO: What is the safe varient?
      positionLinesAt(0, alignmentContext, lines, crossGap);
      return;
    }

    float distSize = spaceLeft / (lines.size() - 1);
    positionLinesAt(0, alignmentContext, lines, distSize + crossGap);
  }

  private static void positionLinesAround(
    CrossAlignmentContext alignmentContext, List<FlexLine> lines, float spaceLeft
  ) {
    float crossGap = alignmentContext.crossGap();
    if (spaceLeft < 0 || lines.size() == 1) {
      positionLinesAt(spaceLeft / 2, alignmentContext, lines, crossGap);
      return;
    }

    float distSize = spaceLeft / lines.size();
    positionLinesAt(distSize / 2, alignmentContext, lines, distSize + crossGap);
  }

  private static float computeRemainingFreeSpace(
    CrossAlignmentContext alignmentContext, List<FlexLine> lines
  ) {
    // TODO: Handle the case where cross size not specified
    float remainingFreeSpace = alignmentContext.crossSize().value();
    remainingFreeSpace -= alignmentContext.crossGap();
    for (FlexLine line: lines) {
      remainingFreeSpace -= line.crossSize();
    }

    return Math.max(0, remainingFreeSpace);
  }

  private static void setCrossPos(FlexLine line, float startPos, boolean isVertical) {
    for (FlexItem item: line.items()) {
      // TODO: Handle auto margin
      float[] margin = item.box().dimensions().getComputedMargin();
      if (isVertical) {
        float newX = item.fragment().posX(Measurement.BORDER) + startPos;
        item.fragment().setPos(newX + margin[2], item.fragment().posY(Measurement.BORDER));
      } else {
        float newY = item.fragment().posY(Measurement.BORDER) + startPos;
        item.fragment().setPos(item.fragment().posX(Measurement.BORDER), newY + margin[0]);
      }
    }
  }

  public static record CrossAlignmentContext(
    LayoutConstraint crossSize,
    boolean isVertical,
    AlignItemsValue alignItems,
    AlignContentValue alignContent,
    float crossGap
  ) {}

}
