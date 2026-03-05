package net.buildabrowser.babbrowser.browser.render.content.flexbox;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.content.common.SizingUtil;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.JustifyContentValue;

public final class FlexMainAlignment {
  
  private FlexMainAlignment() {}

  public static void alignMainAxis(
    MainAlignmentContext alignmentContext, List<FlexLine> lines
  ) {
    for (FlexLine line: lines) {
      float autoSize = distributeMainSpace(alignmentContext, line);
      if (
        alignmentContext.isReverse()
        && alignmentContext.mainSize().isBounded() // The final pass should be bounded, earlier passes should not matter so much
      ) {
        justifyItemsReverse(alignmentContext, line, autoSize);
      } else {
        justifyItems(alignmentContext, line, autoSize);
      }
    }
  }

  private static float distributeMainSpace(MainAlignmentContext alignmentContext, FlexLine line) {
    if (!alignmentContext.mainSize().isBounded()) return 0;

    int[] numAutos = new int[1];
    float remainingFreeSpace = computeRemainingFreeSpace(alignmentContext, line, 0, numAutos);

    if (
      remainingFreeSpace <= 0
      || numAutos[0] == 0
    ) return 0;

    return remainingFreeSpace / numAutos[0];
  }

  private static void justifyItems(
    MainAlignmentContext alignmentContext, FlexLine line, float autoSize
  ) {
    // TODO: But does the align affect overflow?
    float spaceLeft = computeRemainingFreeSpace(alignmentContext, line, autoSize, new int[1]);
    switch (alignmentContext.justification()) {
      case FLEX_START -> positionItemsAt(0, alignmentContext, line, autoSize, 0);
      case FLEX_END -> positionItemsAt(spaceLeft, alignmentContext, line, autoSize, 0);
      case CENTER -> positionItemsAt(spaceLeft / 2, alignmentContext, line, autoSize, 0);
      case SPACE_BETWEEN -> positionItemsBetween(alignmentContext, line, autoSize, spaceLeft);
      case SPACE_AROUND -> positionItemsAround(alignmentContext, line, autoSize, spaceLeft);
      default -> throw new UnsupportedOperationException("Unsupported justification!");
    }
  }

  private static void justifyItemsReverse(
    MainAlignmentContext alignmentContext, FlexLine line, float autoSize
  ) {
    float lineSize = alignmentContext.mainSize().value();
    float spaceLeft = computeRemainingFreeSpace(alignmentContext, line, autoSize, new int[1]);
    switch (alignmentContext.justification()) {
      case FLEX_START -> positionItemsAtReverse(lineSize, alignmentContext, line, autoSize, 0);
      case FLEX_END -> positionItemsAtReverse(lineSize - spaceLeft, alignmentContext, line, autoSize, 0);
      case CENTER -> positionItemsAtReverse(lineSize - spaceLeft / 2, alignmentContext, line, autoSize, 0);
      case SPACE_BETWEEN -> positionItemsBetweenReverse(alignmentContext, line, lineSize, autoSize, spaceLeft);
      case SPACE_AROUND -> positionItemsAroundReverse(alignmentContext, line, lineSize, autoSize, spaceLeft);
      default -> throw new UnsupportedOperationException("Unsupported justification!");
    }
  }

  private static void positionItemsAt(
    float startPos, MainAlignmentContext alignmentContext, FlexLine line, float autoSize, float gapSize
  ) {
    for (FlexItem item: line.items()) {
      LayoutConstraint firstMargin = firstMargin(alignmentContext, item);
      startPos += firstMargin.isBounded() ? firstMargin.value() : autoSize;
      setMainPos(item, startPos, alignmentContext.isVertical());
      startPos += item.mainSize();
      LayoutConstraint secondMargin = secondMargin(alignmentContext, item);
      startPos += secondMargin.isBounded() ? secondMargin.value() : autoSize;
      startPos += gapSize;
    }
  }

  private static void positionItemsAtReverse(
    float startPos, MainAlignmentContext alignmentContext, FlexLine line, float autoSize, float gapSize
  ) {
    for (FlexItem item: line.items()) {
      LayoutConstraint secondMargin = secondMargin(alignmentContext, item);
      startPos -= secondMargin.isBounded() ? secondMargin.value() : autoSize;
      startPos -= item.mainSize();
      setMainPos(item, startPos, alignmentContext.isVertical());
      LayoutConstraint firstMargin = firstMargin(alignmentContext, item);
      startPos -= firstMargin.isBounded() ? firstMargin.value() : autoSize;
      startPos -= gapSize;
    }
  }

  private static void positionItemsBetween(
    MainAlignmentContext alignmentContext, FlexLine line, float autoSize, float spaceLeft
  ) {
    if (spaceLeft < 0 || line.items().size() == 1) {
      // TODO: What is the safe variant?
      positionItemsAt(0, alignmentContext, line, autoSize, 0);
      return;
    }

    float distSize = spaceLeft / (line.items().size() - 1);
    positionItemsAt(0, alignmentContext, line, autoSize, distSize);
  }

  private static void positionItemsAround(
    MainAlignmentContext alignmentContext, FlexLine line, float autoSize, float spaceLeft
  ) {
    if (spaceLeft < 0 || line.items().size() == 1) {
      positionItemsAt(spaceLeft / 2, alignmentContext, line, autoSize, 0);
      return;
    }

    float distSize = spaceLeft / line.items().size();
    positionItemsAt(distSize / 2, alignmentContext, line, autoSize, distSize);
  }
  
  private static void positionItemsBetweenReverse(
    MainAlignmentContext alignmentContext, FlexLine line, float startPos, float autoSize, float spaceLeft
  ) {
    if (spaceLeft < 0 || line.items().size() == 1) {
      positionItemsAtReverse(startPos, alignmentContext, line, autoSize, 0);
      return;
    }

    float distSize = spaceLeft / (line.items().size() - 1);
    positionItemsAtReverse(startPos, alignmentContext, line, autoSize, distSize);
  }

  private static void positionItemsAroundReverse(
    MainAlignmentContext alignmentContext, FlexLine line, float startPos, float autoSize, float spaceLeft
  ) {
    if (spaceLeft < 0 || line.items().size() == 1) {
      positionItemsAtReverse(startPos - spaceLeft / 2, alignmentContext, line, autoSize, 0);
      return;
    }

    float distSize = spaceLeft / line.items().size();
    positionItemsAtReverse(startPos - distSize / 2, alignmentContext, line, autoSize, distSize);
  }

  private static float computeRemainingFreeSpace(
    MainAlignmentContext alignmentContext, FlexLine line,
    float autoSize, int[] numAutos // Annoying output param to avoid duplicating this code for distributeMainSpace
  ) {
    float remainingFreeSpace = alignmentContext.mainSize().value();
    for (FlexItem item: line.items()) {
      remainingFreeSpace -= item.mainSize();
      
      LayoutConstraint firstMargin = firstMargin(alignmentContext, item);
      if (firstMargin.isBounded()) {
        remainingFreeSpace -= firstMargin.value();
      } else {
        remainingFreeSpace -= autoSize;
        numAutos[0]++;
      }
      
      LayoutConstraint secondMargin = secondMargin(alignmentContext, item);
      if (secondMargin.isBounded()) {
        remainingFreeSpace -= secondMargin.value();
      } else {
        remainingFreeSpace -= autoSize;
        numAutos[0]++;
      }
    }

    return remainingFreeSpace;
  }

  private static LayoutConstraint firstMargin(
    MainAlignmentContext alignmentContext, FlexItem item
  ) {
    ActiveStyles activeStyles = item.box().activeStyles();
    CSSValue relevantValue = alignmentContext.isVertical() ?
      activeStyles.getProperty(CSSProperty.MARGIN_TOP) :
      activeStyles.getProperty(CSSProperty.MARGIN_LEFT);
    return SizingUtil.evaluateBaseSize(
      alignmentContext.layoutContext(), alignmentContext.mainSize(), relevantValue);
  }

  private static LayoutConstraint secondMargin(
    MainAlignmentContext alignmentContext, FlexItem item
  ) {
    ActiveStyles activeStyles = item.box().activeStyles();
    CSSValue relevantValue = alignmentContext.isVertical() ?
      activeStyles.getProperty(CSSProperty.MARGIN_BOTTOM) :
      activeStyles.getProperty(CSSProperty.MARGIN_RIGHT);
    return SizingUtil.evaluateBaseSize(
      alignmentContext.layoutContext(), alignmentContext.mainSize(), relevantValue);
  }

  private static void setMainPos(FlexItem item, float startPos, boolean isVertical) {
    // Hopefully the other value wasn't set it. Attempting to get it here could cause an assertion error.
    if (isVertical) {
      item.fragment().setPos(0, startPos);
    } else {
      item.fragment().setPos(startPos, 0);
    }
  }

  public static record MainAlignmentContext(
    LayoutContext layoutContext, LayoutConstraint mainSize,
    boolean isVertical, boolean isReverse, JustifyContentValue justification
  ) {}

}
