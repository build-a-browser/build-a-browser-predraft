package net.buildabrowser.babbrowser.renderer.content.flexbox;

import java.util.List;
import java.util.function.Function;

import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class FlexMainIntrinsicSizing {

  public static float determineWebCompatibleSize(
    LayoutConstraint crossSize,
    List<FlexItem> flexItems,
    boolean isMinContent,
    boolean isMultiLine,
    float mainGap
  ) {
    float sum = 0;
    for (FlexItem item : flexItems) {
      float contribution = isMinContent ?
        item.minContentContribution(crossSize) :
        item.maxContentContribution(crossSize);
      float outerContribution = contribution + item.mainMargin();
      item.setTargetMainSize(contribution);

      if (!isMultiLine) {
        sum += outerContribution;
      } else {
        sum = Math.max(sum, outerContribution);
      }
    }

    if (!isMultiLine) {
      sum += mainGap * (flexItems.size() - 1);
    }

    return sum;
  }

  public static float determineIdealSize(
    LayoutConstraint crossSize,
    List<FlexItem> flexItems,
    boolean isMinContent
  ) {
    float chosenFlexFraction = Float.NEGATIVE_INFINITY;

    // TODO: In what case would there be multiple lines?
    // TODO: Skip collapsed items
    for (FlexItem item: flexItems) {
      float baseSize = item.baseSize();
      float contribution = isMinContent ?
        item.minContentContribution(crossSize) :
        item.maxContentContribution(crossSize);
      float desiredFlexFraction = contribution - baseSize;
      if (desiredFlexFraction >= 0) {
        float growFactor = item.growFactor();
        if (growFactor >= 1) {
          desiredFlexFraction /= growFactor;
        } else {
          desiredFlexFraction *= growFactor;
        }
      } else {
        float scaledShrinkFactor = item.shrinkFactor() * baseSize;
        desiredFlexFraction /= scaledShrinkFactor;
        if (scaledShrinkFactor == 0) {
          desiredFlexFraction = Float.NEGATIVE_INFINITY;
        }
      }

      chosenFlexFraction = Math.max(chosenFlexFraction, desiredFlexFraction);
    }

    if (chosenFlexFraction > 0) {
      float flexGrow = sumFactors(flexItems, FlexItem::growFactor);
      if (flexGrow < 1) {
        chosenFlexFraction /= flexGrow;
      }
    } else {
      float flexShrink = sumFactors(flexItems, FlexItem::shrinkFactor);
      if (flexShrink < 1) {
        chosenFlexFraction *= flexShrink;
      }
    }

    float mainSizeSum = 0;
    for (FlexItem item: flexItems) {
      float baseSize = item.baseSize();
      // TODO: Is this the proper way to determine if it is shrinking or not?
      float usedFactor = chosenFlexFraction >= 0 ?
        item.growFactor() :
        item.shrinkFactor() * baseSize;
      float mainSize = baseSize + usedFactor * chosenFlexFraction;
      if (
        item.maxMainSize() != null &&
        mainSize > item.maxMainSize()
      ) {
        mainSize = item.maxMainSize();
      }
      if (mainSize < item.minMainSize()) {
        mainSize = item.minMainSize();
      }
      item.setTargetMainSize(mainSize);
      mainSizeSum += mainSize;
    }
    
    return mainSizeSum;
  }
  
  private static float sumFactors(
    List<FlexItem> flexItems,
    Function<FlexItem, Float> factorFunc
  ) {
    float total = 0;
    for (FlexItem item: flexItems) {
      total += factorFunc.apply(item);
    }

    return total;
  }
  
}
