package net.buildabrowser.babbrowser.browser.render.content.flexbox;

import java.util.List;

import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;

public final class Flexer {
  
  private Flexer() {}

  public static void flex(LayoutConstraint mainSize, FlexLine flexLine) {
    // TODO: Better prelayout constraint handling
    if (!mainSize.isPreLayoutConstraint()) return;
    // TODO: For flex items, the below would still grow to the min size, as override by section 4.5
    if (!mainSize.isBounded()) return;
    boolean isGrow = flexLine.sumHypotheticalMainSizes() < mainSize.value();
    if (isGrow) {
      growItems(mainSize, flexLine);
    } else {
      shrinkItems(mainSize, flexLine);
    }
  }

  private static void growItems(LayoutConstraint mainSize, FlexLine flexLine) {
    for (FlexItem item: flexLine.items()) {
      if (
        item.growFactor() == 0
        || item.baseSize() > item.hypotheticalMainSize()
      ) {
        item.setTargetMainSize(item.hypotheticalMainSize());
        item.freeze();
      }
    }

    float initialFreeSpace = calculateInitialFreeSpace(mainSize, flexLine);
    boolean didFreezeItems;
    do {
      float remainingGrowFactor = computeRemainingGrowFactor(flexLine);
      float remainingFreeSpace = calculateRemainingFreeSpace(
        mainSize, flexLine, initialFreeSpace, remainingGrowFactor);
      if (remainingFreeSpace != 0) {
        for (FlexItem item: flexLine.items()) {
          if (item.isFrozen()) continue;
          item.setHypotheticalMainSize(
            item.baseSize() + remainingFreeSpace * item.growFactor() / remainingGrowFactor);
        }
      }

      didFreezeItems = correctViolations(flexLine.items());
    } while (didFreezeItems);
  }

  private static void shrinkItems(LayoutConstraint mainSize, FlexLine flexLine) {
    for (FlexItem item: flexLine.items()) {
      if (
        item.shrinkFactor() == 0
        || item.baseSize() < item.hypotheticalMainSize()
      ) {
        item.setTargetMainSize(item.hypotheticalMainSize());
        item.freeze();
      }
    }

    float initialFreeSpace = calculateInitialFreeSpace(mainSize, flexLine);

    boolean didFreezeItems;
    do {
      float remainingGrowFactor = computeRemainingGrowFactor(flexLine);
      float remainingFreeSpace = calculateRemainingFreeSpace(
        mainSize, flexLine, initialFreeSpace, remainingGrowFactor);
      if (remainingFreeSpace != 0) {
        float scaledShrinkFactorSum = 0;
        for (FlexItem item: flexLine.items()) {
          if (item.isFrozen()) continue;
          scaledShrinkFactorSum += item.shrinkFactor() * item.baseSize();
        }
        for (FlexItem item: flexLine.items()) {
          if (item.isFrozen()) continue;
          float scaledShrinkFactor = item.shrinkFactor() * item.baseSize();
          item.setHypotheticalMainSize(item.baseSize()
            - scaledShrinkFactor / scaledShrinkFactorSum * Math.abs(remainingFreeSpace));
        }
      }

      didFreezeItems = correctViolations(flexLine.items());
    } while (didFreezeItems);
  }

  private static boolean correctViolations(List<FlexItem> items) {
    // TODO: Actually find and correct violations
    boolean didFreezeItems = false;
    for (FlexItem item: items) {
      if (item.isFrozen()) continue;
      didFreezeItems = true;
      item.freeze();
    }

    return didFreezeItems;
  }

  private static float calculateInitialFreeSpace(LayoutConstraint mainSize, FlexLine flexLine) {
    float remainingSpace = mainSize.value();
    for (FlexItem item: flexLine.items()) {
      remainingSpace -= item.isFrozen() ?
        item.mainSize() :
        item.baseSize();
    }
    
    return remainingSpace;
  }

  private static float calculateRemainingFreeSpace(
    LayoutConstraint mainSize, FlexLine flexLine, float initialSpace, float flexFactorSum
  ) {
    float remainingSpace = calculateInitialFreeSpace(mainSize, flexLine);

    if (flexFactorSum >= 1) return remainingSpace;
    return Math.min(remainingSpace, initialSpace * flexFactorSum);
  }

  private static float computeRemainingGrowFactor(FlexLine flexLine) {
    float remainingGrowFactor = 0;
    for (FlexItem item: flexLine.items()) {
      if (item.isFrozen()) continue;
      remainingGrowFactor += item.growFactor();
    }

    return remainingGrowFactor;
  }

}
