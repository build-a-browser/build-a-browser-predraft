package net.buildabrowser.babbrowser.renderer.content.flexbox;

import java.util.List;

import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public final class Flexer {
  
  private Flexer() {}

  public static void flex(
    LayoutConstraint mainSize, FlexLine flexLine, float mainGap
  ) {
    // TODO: Better prelayout constraint handling
    if (mainSize.isPreLayoutConstraint()) return;
    // TODO: For flex items, the below would still grow to the min size, as override by section 4.5
    if (!mainSize.isBounded()) return;
    boolean isGrow = flexLine.sumHypotheticalMainSizes(mainGap) < mainSize.value();
    if (isGrow) {
      growItems(mainSize, flexLine, mainGap);
    } else {
      shrinkItems(mainSize, flexLine, mainGap);
    }
  }

  private static void growItems(
    LayoutConstraint mainSize, FlexLine flexLine, float mainGap
  ) {
    for (FlexItem item: flexLine.items()) {
      if (
        item.growFactor() == 0
        || item.baseSize() > item.hypotheticalMainSize()
      ) {
        item.setTargetMainSize(item.hypotheticalMainSize());
        item.freeze();
      }
    }

    float initialFreeSpace = calculateInitialFreeSpace(mainSize, flexLine, mainGap);
    boolean didFreezeItems;
    do {
      float remainingGrowFactor = computeRemainingFactor(flexLine, true);
      float remainingFreeSpace = calculateRemainingFreeSpace(
        mainSize, flexLine, initialFreeSpace, remainingGrowFactor, mainGap);
      if (remainingFreeSpace != 0) {
        for (FlexItem item: flexLine.items()) {
          if (item.isFrozen()) continue;
          item.setTargetMainSize(
            item.baseSize() + remainingFreeSpace * item.growFactor() / remainingGrowFactor);
        }
      }

      didFreezeItems = correctViolations(flexLine.items());
    } while (didFreezeItems);
  }

  private static void shrinkItems(
    LayoutConstraint mainSize, FlexLine flexLine, float mainGap
  ) {
    for (FlexItem item: flexLine.items()) {
      if (
        item.shrinkFactor() == 0
        || item.baseSize() < item.hypotheticalMainSize()
      ) {
        item.setTargetMainSize(item.hypotheticalMainSize());
        item.freeze();
      }
    }

    float initialFreeSpace = calculateInitialFreeSpace(mainSize, flexLine, mainGap);

    boolean didFreezeItems;
    do {
      float remainingShrinkFactor = computeRemainingFactor(flexLine, false);
      float remainingFreeSpace = calculateRemainingFreeSpace(
        mainSize, flexLine, initialFreeSpace, remainingShrinkFactor, mainGap);
      if (remainingFreeSpace != 0) {
        float scaledShrinkFactorSum = 0;
        for (FlexItem item: flexLine.items()) {
          if (item.isFrozen()) continue;
          scaledShrinkFactorSum += item.shrinkFactor() * item.baseSize();
        }
        for (FlexItem item: flexLine.items()) {
          if (item.isFrozen()) continue;
          float scaledShrinkFactor = item.shrinkFactor() * item.baseSize();
          item.setTargetMainSize(item.baseSize()
            - scaledShrinkFactor / scaledShrinkFactorSum * Math.abs(remainingFreeSpace));
        }
      }

      didFreezeItems = correctViolations(flexLine.items());
    } while (didFreezeItems);
  }

  private static boolean correctViolations(List<FlexItem> items) {
    float totalViolation = 0;
    for (FlexItem item: items) {
      if (item.mainSize() < item.minMainSize()) {
        totalViolation += item.minMainSize() - item.mainSize();
      } else if (
        item.maxMainSize() != null
        && item.mainSize() > item.maxMainSize()
      ) {
        totalViolation += item.maxMainSize() - item.mainSize();
      }
    }

    boolean didFreezeItems = false;
    for (FlexItem item: items) {
      if (item.isFrozen()) continue;
      didFreezeItems = true;

      if (item.mainSize() < item.minMainSize()) {
        item.setTargetMainSize(item.minMainSize());
        if (totalViolation > 0) {
          item.freeze();
        }
      } else if (
        item.maxMainSize() != null &&
        item.mainSize() > item.maxMainSize()
      ) {
        item.setTargetMainSize(item.maxMainSize());
        if (totalViolation < 0) {
          item.freeze();
        }
      }

      if (totalViolation == 0) {
        item.freeze();
      }
    }

    return didFreezeItems;
  }

  private static float calculateInitialFreeSpace(LayoutConstraint mainSize, FlexLine flexLine, float mainGap) {
    float remainingSpace = mainSize.value();
    for (FlexItem item: flexLine.items()) {
      remainingSpace -= item.isFrozen() ?
        item.outerSize(item.mainSize()) :
        item.outerSize(item.baseSize());
    }
    remainingSpace -= mainGap * (flexLine.items().size() - 1);
    
    return remainingSpace;
  }

  private static float calculateRemainingFreeSpace(
    LayoutConstraint mainSize, FlexLine flexLine, float initialSpace, float flexFactorSum,
    float mainGap
  ) {
    float remainingSpace = calculateInitialFreeSpace(mainSize, flexLine, mainGap);

    if (flexFactorSum >= 1) return remainingSpace;
    return Math.min(remainingSpace, initialSpace * flexFactorSum);
  }

  private static float computeRemainingFactor(FlexLine flexLine, boolean isGrow) {
    float remainingFactor = 0;
    for (FlexItem item: flexLine.items()) {
      if (item.isFrozen()) continue;
      remainingFactor += isGrow ?
        item.growFactor() :
        item.shrinkFactor();
    }

    return remainingFactor;
  }

}
