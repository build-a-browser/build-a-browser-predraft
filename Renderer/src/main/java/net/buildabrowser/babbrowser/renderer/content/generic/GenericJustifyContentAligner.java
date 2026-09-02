package net.buildabrowser.babbrowser.renderer.content.generic;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.align.JustifyContentValue;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

// justify-content, gap (main)
public final class GenericJustifyContentAligner {
  
  private GenericJustifyContentAligner() {}

  public static void justifyContents(
    MainAlignmentContext alignmentContext, List<GenericJustifyContentItem> contents
  ) {
    Line line = new Line(contents);
    float autoSize = distributeMainSpace(alignmentContext, line);
    if (
      alignmentContext.isReverse()
      && !alignmentContext.mainSize().isPreLayoutConstraint() // The final pass should be bounded, earlier passes should not matter so much
    ) {
      justifyContentReverse(alignmentContext, line, autoSize);
    } else {
      justifyContent(alignmentContext, line, autoSize);
    }
  }

  public static float computeFitSpace(
    List<GenericJustifyContentItem> contents, float mainGap
  ) {
    LayoutConstraint parentConstraint = LayoutConstraint.AUTO;

    float fitSpace = 0;
    fitSpace += mainGap * (contents.size() - 1);
    for (GenericJustifyContentItem item: contents) {
      fitSpace += item.mainSize();
      
      LayoutConstraint firstMargin = item.firstMargin(parentConstraint);
      if (firstMargin.isBounded()) {
        fitSpace += firstMargin.value();
      }
      
      LayoutConstraint secondMargin = item.secondMargin(parentConstraint);
      if (secondMargin.isBounded()) {
        fitSpace += secondMargin.value();
      }
    }

    return fitSpace;
  }

  private static float distributeMainSpace(MainAlignmentContext alignmentContext, Line line) {
    if (!alignmentContext.mainSize().isBounded()) return 0;

    int[] numAutos = new int[1];
    float remainingFreeSpace = computeRemainingFreeSpace(alignmentContext, line, 0, numAutos);

    if (
      remainingFreeSpace <= 0
      || numAutos[0] == 0
    ) return 0;

    return remainingFreeSpace / numAutos[0];
  }

  private static void justifyContent(
    MainAlignmentContext alignmentContext, Line line, float autoSize
  ) {
    // TODO: But does the align affect overflow?
    float gapSize = alignmentContext.mainGap();
    float spaceLeft = computeRemainingFreeSpace(alignmentContext, line, autoSize, new int[1]);
    JustifyContentValue justification = alignmentContext.justification().equals(JustifyContentValue.NORMAL) ?
      alignmentContext.fallback() : alignmentContext.justification();
    // TODO: Edge cases for START, END, properly support STRETCH
    switch (justification) {
      case START, FLEX_START, STRETCH -> positionItemsAt(0, alignmentContext, line, autoSize, gapSize);
      case END, FLEX_END -> positionItemsAt(spaceLeft, alignmentContext, line, autoSize, gapSize);
      case CENTER -> positionItemsAt(spaceLeft / 2, alignmentContext, line, autoSize, gapSize);
      case SPACE_BETWEEN -> positionItemsBetween(alignmentContext, line, autoSize, spaceLeft);
      case SPACE_AROUND -> positionItemsAround(alignmentContext, line, autoSize, spaceLeft);
      case SPACE_EVENLY -> positionItemsEvenly(alignmentContext, line, autoSize, spaceLeft);
      default -> throw new UnsupportedOperationException("Unsupported justification: " + justification);
    }
  }

  private static void justifyContentReverse(
    MainAlignmentContext alignmentContext, Line line, float autoSize
  ) {
    float gapSize = alignmentContext.mainGap();
    float lineSize = alignmentContext.mainSize().value();
    float spaceLeft = computeRemainingFreeSpace(alignmentContext, line, autoSize, new int[1]);
    JustifyContentValue justification = alignmentContext.justification().equals(JustifyContentValue.NORMAL) ?
      alignmentContext.fallback() : alignmentContext.justification();
    switch (justification) {
      case START, FLEX_START, STRETCH -> positionItemsAtReverse(lineSize, alignmentContext, line, autoSize, gapSize);
      case END, FLEX_END -> positionItemsAtReverse(lineSize - spaceLeft, alignmentContext, line, autoSize, gapSize);
      case CENTER -> positionItemsAtReverse(lineSize - spaceLeft / 2, alignmentContext, line, autoSize, gapSize);
      case SPACE_BETWEEN -> positionItemsBetweenReverse(alignmentContext, line, lineSize, autoSize, spaceLeft);
      case SPACE_AROUND -> positionItemsAroundReverse(alignmentContext, line, lineSize, autoSize, spaceLeft);
      case SPACE_EVENLY -> positionItemsEvenlyReverse(alignmentContext, line, lineSize, autoSize, spaceLeft);
      default -> throw new UnsupportedOperationException("Unsupported justification: " + justification);
    }
  }

  private static void positionItemsAt(
    float startPos, MainAlignmentContext alignmentContext, Line line, float autoSize, float gapSize
  ) {
    LayoutConstraint parentConstraint = alignmentContext.mainSize();

    for (GenericJustifyContentItem item: line.genericItems()) {
      LayoutConstraint firstMargin = item.firstMargin(parentConstraint);
      startPos += firstMargin.isBounded() ? firstMargin.value() : autoSize;
      item.setMainPos(startPos);
      startPos += item.mainSize();
      LayoutConstraint secondMargin = item.secondMargin(parentConstraint);
      startPos += secondMargin.isBounded() ? secondMargin.value() : autoSize;
      startPos += gapSize;
    }
  }

  private static void positionItemsAtReverse(
    float startPos, MainAlignmentContext alignmentContext, Line line, float autoSize, float gapSize
  ) {
    LayoutConstraint parentConstraint = alignmentContext.mainSize();
    for (GenericJustifyContentItem item: line.genericItems()) {
      LayoutConstraint secondMargin = item.secondMargin(parentConstraint);
      startPos -= secondMargin.isBounded() ? secondMargin.value() : autoSize;
      startPos -= item.mainSize();
      item.setMainPos(startPos);
      LayoutConstraint firstMargin = item.firstMargin(parentConstraint);
      startPos -= firstMargin.isBounded() ? firstMargin.value() : autoSize;
      startPos -= gapSize;
    }
  }

  private static void positionItemsBetween(
    MainAlignmentContext alignmentContext, Line line, float autoSize, float spaceLeft
  ) {
    float gapSize = alignmentContext.mainGap();
    if (spaceLeft < 0 || line.genericItems().size() == 1) {
      // TODO: What is the safe variant?
      positionItemsAt(0, alignmentContext, line, autoSize, gapSize);
      return;
    }

    float distSize = spaceLeft / (line.genericItems().size() - 1);
    positionItemsAt(0, alignmentContext, line, autoSize, distSize + gapSize);
  }

  private static void positionItemsAround(
    MainAlignmentContext alignmentContext, Line line, float autoSize, float spaceLeft
  ) {
    float gapSize = alignmentContext.mainGap();
    if (spaceLeft < 0) {
      positionItemsAt(0, alignmentContext, line, autoSize, gapSize);
      return;
    }

    float distSize = spaceLeft / line.genericItems().size();
    positionItemsAt(distSize / 2, alignmentContext, line, autoSize, distSize + gapSize);
  }

  private static void positionItemsEvenly(
    MainAlignmentContext alignmentContext, Line line, float autoSize, float spaceLeft
  ) {
    float gapSize = alignmentContext.mainGap();
    if (spaceLeft < 0) {
      positionItemsAt(0, alignmentContext, line, autoSize, gapSize);
      return;
    }

    float distSize = spaceLeft / (line.genericItems().size() + 1);
    positionItemsAt(distSize, alignmentContext, line, autoSize, distSize + gapSize);
  }
  
  private static void positionItemsBetweenReverse(
    MainAlignmentContext alignmentContext, Line line, float startPos, float autoSize, float spaceLeft
  ) {
    float gapSize = alignmentContext.mainGap();
    if (spaceLeft < 0 || line.genericItems().size() == 1) {
      positionItemsAtReverse(startPos, alignmentContext, line, autoSize, gapSize);
      return;
    }

    float distSize = spaceLeft / (line.genericItems().size() - 1);
    positionItemsAtReverse(startPos, alignmentContext, line, autoSize, distSize + gapSize);
  }

  private static void positionItemsAroundReverse(
    MainAlignmentContext alignmentContext, Line line, float startPos, float autoSize, float spaceLeft
  ) {
    float gapSize = alignmentContext.mainGap();
    if (spaceLeft < 0) {
      positionItemsAtReverse(startPos, alignmentContext, line, autoSize, gapSize);
      return;
    }

    float distSize = spaceLeft / line.genericItems().size();
    positionItemsAtReverse(startPos - distSize / 2, alignmentContext, line, autoSize, distSize + gapSize);
  }

  private static void positionItemsEvenlyReverse(
    MainAlignmentContext alignmentContext, Line line, float startPos, float autoSize, float spaceLeft
  ) {
    float gapSize = alignmentContext.mainGap();
    if (spaceLeft < 0) {
      positionItemsAtReverse(startPos, alignmentContext, line, autoSize, gapSize);
      return;
    }

    float distSize = spaceLeft / (line.genericItems().size() + 1);
    positionItemsAtReverse(startPos - distSize, alignmentContext, line, autoSize, distSize + gapSize);
  }

  private static float computeRemainingFreeSpace(
    MainAlignmentContext alignmentContext, Line line,
    float autoSize, int[] numAutos // Annoying output param to avoid duplicating this code for distributeMainSpace
  ) {
    LayoutConstraint parentConstraint = alignmentContext.mainSize();

    float remainingFreeSpace = parentConstraint.value();
    remainingFreeSpace -= alignmentContext.mainGap() * (line.genericItems().size() - 1);
    for (GenericJustifyContentItem item: line.genericItems()) {
      remainingFreeSpace -= item.mainSize();
      
      LayoutConstraint firstMargin = item.firstMargin(parentConstraint);
      if (firstMargin.isBounded()) {
        remainingFreeSpace -= firstMargin.value();
      } else {
        remainingFreeSpace -= autoSize;
        numAutos[0]++;
      }
      
      LayoutConstraint secondMargin = item.secondMargin(parentConstraint);
      if (secondMargin.isBounded()) {
        remainingFreeSpace -= secondMargin.value();
      } else {
        remainingFreeSpace -= autoSize;
        numAutos[0]++;
      }
    }

    if (!parentConstraint.isBounded()) {
      return 0;
    }

    return remainingFreeSpace;
  }

  public static record MainAlignmentContext(
    LayoutConstraint mainSize, float mainGap,
    boolean isVertical, boolean isReverse,
    JustifyContentValue justification, JustifyContentValue fallback
  ) {}

  private static record Line(
    List<GenericJustifyContentItem> genericItems
  ) {}

}
