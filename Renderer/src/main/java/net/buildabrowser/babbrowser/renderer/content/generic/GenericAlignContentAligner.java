package net.buildabrowser.babbrowser.renderer.content.generic;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.align.AlignContentValue;
import net.buildabrowser.babbrowser.cssbase.property.align.AlignItemsValue;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

// align-content, gap (cross)
public class GenericAlignContentAligner {
  
  private GenericAlignContentAligner() {}

  public static void alignLines(
    CrossAlignmentContext alignmentContext, List<GenericTrack> lines
  ) {
    // TODO: But does the align affect overflow?
    float gapSize = alignmentContext.crossGap();
    float spaceLeft = computeRemainingFreeSpace(alignmentContext, lines);
    // TODO: Edge cases for start, end, and normal
    switch (alignmentContext.alignContent()) {
      case START, FLEX_START -> positionLinesAt(0, alignmentContext, lines, gapSize);
      case END, FLEX_END -> positionLinesAt(spaceLeft, alignmentContext, lines, gapSize);
      case CENTER -> positionLinesAt(spaceLeft / 2, alignmentContext, lines, gapSize);
      case SPACE_BETWEEN -> positionLinesBetween(alignmentContext, lines, spaceLeft);
      case SPACE_AROUND -> positionLinesAround(alignmentContext, lines, spaceLeft);
      case SPACE_EVENLY -> positionLinesEvenly(alignmentContext, lines, spaceLeft);
      case NORMAL, STRETCH -> positionLinesAt(0, alignmentContext, lines, gapSize);
      default -> throw new UnsupportedOperationException("Unsupported alignment!");
    }
  }

  private static void positionLinesAt(
    float startPos, CrossAlignmentContext alignmentContext, List<GenericTrack> lines, float gapSize
  ) {
    for (GenericTrack line: lines) {
      line.setCrossPos(startPos);
      startPos += line.crossSize();
      startPos += gapSize;
    }
  }

  private static void positionLinesBetween(
    CrossAlignmentContext alignmentContext, List<GenericTrack> lines, float spaceLeft
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
    CrossAlignmentContext alignmentContext, List<GenericTrack> lines, float spaceLeft
  ) {
    float crossGap = alignmentContext.crossGap();
    if (spaceLeft < 0) {
      positionLinesAt(0, alignmentContext, lines, crossGap);
      return;
    }

    float distSize = spaceLeft / lines.size();
    positionLinesAt(distSize / 2, alignmentContext, lines, distSize + crossGap);
  }

  private static void positionLinesEvenly(
    CrossAlignmentContext alignmentContext, List<GenericTrack> lines, float spaceLeft
  ) {
    float crossGap = alignmentContext.crossGap();
    if (spaceLeft < 0) {
      positionLinesAt(0, alignmentContext, lines, crossGap);
      return;
    }

    float distSize = spaceLeft / (lines.size() + 1);
    positionLinesAt(distSize, alignmentContext, lines, distSize + crossGap);
  }

  private static float computeRemainingFreeSpace(
    CrossAlignmentContext alignmentContext, List<GenericTrack> lines
  ) {
    // TODO: Handle the case where cross size not specified
    float remainingFreeSpace = alignmentContext.crossSize().value();
    remainingFreeSpace -= alignmentContext.crossGap();
    for (GenericTrack line: lines) {
      remainingFreeSpace -= line.crossSize();
    }

    return Math.max(0, remainingFreeSpace);
  }

  public static record CrossAlignmentContext(
    LayoutConstraint crossSize,
    float crossGap,
    boolean isVertical,
    AlignItemsValue alignItems,
    AlignContentValue alignContent
  ) {}

}
