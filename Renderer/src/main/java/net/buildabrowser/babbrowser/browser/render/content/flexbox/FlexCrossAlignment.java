package net.buildabrowser.babbrowser.browser.render.content.flexbox;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignContentValue;

public class FlexCrossAlignment {
  
  private FlexCrossAlignment() {}

  public static void alignCrossAxis(
    CrossAlignmentContext alignmentContext, List<FlexLine> lines
  ) {
    alignLines(alignmentContext, lines);
    // TODO: Align items within the lines
  }

  private static void alignLines(
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
        float newX = item.fragment().borderX() + startPos;
        item.fragment().setPos(newX + margin[2], item.fragment().borderY());
      } else {
        float newY = item.fragment().borderY() + startPos;
        item.fragment().setPos(item.fragment().borderX(), newY + margin[0]);
      }
    }
  }

  public static record CrossAlignmentContext(
    LayoutConstraint crossSize, boolean isVertical, AlignContentValue alignContent, float crossGap
  ) {}

}
