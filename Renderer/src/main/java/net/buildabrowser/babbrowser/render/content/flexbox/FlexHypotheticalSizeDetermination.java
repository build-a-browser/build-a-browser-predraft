package net.buildabrowser.babbrowser.render.content.flexbox;

import java.util.List;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexBasisValue;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue;
import net.buildabrowser.babbrowser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint.LayoutConstraintType;

public final class FlexHypotheticalSizeDetermination {
  
  private FlexHypotheticalSizeDetermination() {}

  public static void determineBaseAndHypotheticalSizes(
    List<FlexItem> items,
    LayoutConstraint mainSize, LayoutConstraint crossSize,
    boolean isVertical
  ) {
    for (FlexItem item: items) {
      ElementBoxDimensions itemDimensions = item.box().dimensions();
      
      CSSValue flexBasis = determineUsedBasis(item, isVertical);
      LayoutConstraint basisConstraint = FlexUtil.evaluateFlexBasis(
        item.box(), mainSize, flexBasis, isVertical);
      if (basisConstraint.isBounded()) {
        item.setBaseSize(basisConstraint.value());
        continue;
      }

      LayoutConstraint itemCrossSize = FlexUtil.boxCrossSize(item.box(), crossSize, isVertical);
      if (
        flexBasis.equals(FlexBasisValue.CONTENT)
        && itemCrossSize.isBounded()
        && itemDimensions.intrinsicRatio() != -1
      ) {
        // TODO: Ensure this is supposed to be division
        item.setBaseSize(itemCrossSize.value() / itemDimensions.intrinsicRatio());
        continue;
      }

      // Bit hacky, but refConstraint tells us if the value would resolve given finite space
      LayoutConstraint refConstraint = FlexUtil.evaluateFlexBasis(
        item.box(), LayoutConstraint.of(1),
        flexBasis, isVertical);
      boolean dependsOnAvailableSpace = refConstraint.isBounded();

      // TODO: This currently only handles !isVertical, and lets other cases fall through
      // It should handle isVertical too (maybe cache the min/max vertically too)
      if (
        (flexBasis.equals(FlexBasisValue.CONTENT) || dependsOnAvailableSpace)
        && mainSize.isPreLayoutConstraint()
        && !isVertical
      ) {
        item.setBaseSize(mainSize.type().equals(LayoutConstraintType.MAX_CONTENT) ?
          itemDimensions.preferredWidthConstraint() :
          itemDimensions.preferredMinWidthConstraint());
        continue;
      }

      // TODO: The !isVertical check will need updated once vertical writing modes are supported
      if (
        (flexBasis.equals(FlexBasisValue.CONTENT) || dependsOnAvailableSpace)
        && (
          mainSize.type().equals(LayoutConstraintType.MAX_CONTENT)
          || mainSize.type().equals(LayoutConstraintType.AUTO))
        && !isVertical
      ) {
        item.setBaseSize(itemDimensions.preferredWidthConstraint());
        continue;
      }

      // Case E is tricky...
      // TODO: Account for the preferred aspect ratio
      if (!isVertical) {
        // Knock out some simple cases
        if (
          flexBasis.equals(FlexBasisValue.CONTENT)
          || flexBasis.equals(SizeValue.MAX_CONTENT)
        ) {
          item.setBaseSize(itemDimensions.preferredWidthConstraint());
          continue;
        } else if (flexBasis.equals(SizeValue.MIN_CONTENT)) {
          item.setBaseSize(itemDimensions.preferredMinWidthConstraint());
          continue;
        }
      }

      // I can't think of any other !isVertical cases
      assert isVertical;

      float fitContent = !crossSize.isBounded() ?
        itemDimensions.preferredWidthConstraint() :
        Math.min(
          itemDimensions.preferredWidthConstraint(),
          Math.max(itemDimensions.preferredMinWidthConstraint(), crossSize.value()));

      // TODO: The guard should only apply for auto?
      LayoutConstraint usedCrossSize = !itemCrossSize.isBounded() ?
        LayoutConstraint.of(fitContent) :
        itemCrossSize;
      
      UnmanagedBoxFragment fragmentAtCross = item.box().layout(usedCrossSize, LayoutConstraint.AUTO);
      item.setBaseSize(fragmentAtCross.contentHeight());
    }
    for (FlexItem item: items) {
      // TODO: Clamp the size
      item.setHypotheticalMainSize(item.baseSize());
    }
  }

  private static CSSValue determineUsedBasis(FlexItem item, boolean isVertical) {
    ActiveStyles activeStyles = item.box().activeStyles();
    CSSValue basis = activeStyles.getProperty(CSSProperty.FLEX_BASIS);
    if (!basis.equals(CSSValue.AUTO)) return basis;
    basis = isVertical ?
      activeStyles.getProperty(CSSProperty.HEIGHT) :
      activeStyles.getProperty(CSSProperty.WIDTH);
    if (!basis.equals(CSSValue.AUTO)) return basis;
    return FlexBasisValue.CONTENT;
  }

}
