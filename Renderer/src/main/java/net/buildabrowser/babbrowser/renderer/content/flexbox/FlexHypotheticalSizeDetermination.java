package net.buildabrowser.babbrowser.renderer.content.flexbox;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexBasisValue;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue;
import net.buildabrowser.babbrowser.renderer.box.EBDimensionsUtil;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint.LayoutConstraintType;

public final class FlexHypotheticalSizeDetermination {
  
  private FlexHypotheticalSizeDetermination() {}

  public static void determineBaseAndHypotheticalSizes(
    ElementBox rootBox, List<FlexItem> items,
    LayoutConstraint mainSize, LayoutConstraint crossSize,
    boolean isVertical
  ) {
    for (FlexItem item: items) {
      ElementBox itemBox = item.box();
      ElementBoxDimensions itemDimensions = itemBox.dimensions();
      
      CSSValue flexBasis = determineUsedBasis(item, isVertical);
      LayoutConstraint basisConstraint = FlexUtil.evaluateFlexBasis(
        itemBox, mainSize, flexBasis, isVertical);
      if (basisConstraint.isBounded()) {
        item.setBaseSize(basisConstraint.value());
        continue;
      }

      LayoutConstraint itemCrossSize = FlexUtil.boxCrossSize(
        rootBox, itemBox, crossSize, isVertical);
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
        itemBox, LayoutConstraint.of(1),
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
          EBDimensionsUtil.preferredWidthConstraint(itemBox) :
          EBDimensionsUtil.preferredMinWidthConstraint(itemBox));
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
        item.setBaseSize(EBDimensionsUtil.preferredWidthConstraint(itemBox));
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
          item.setBaseSize(EBDimensionsUtil.preferredWidthConstraint(itemBox));
          continue;
        } else if (flexBasis.equals(SizeValue.MIN_CONTENT)) {
          item.setBaseSize(EBDimensionsUtil.preferredMinWidthConstraint(itemBox));
          continue;
        }
      }

      // I can't think of any other !isVertical cases
      assert isVertical;

      float fitContent = !crossSize.isBounded() ?
        EBDimensionsUtil.preferredWidthConstraint(itemBox) :
        Math.min(
          EBDimensionsUtil.preferredWidthConstraint(itemBox),
          Math.max(EBDimensionsUtil.preferredMinWidthConstraint(itemBox), crossSize.value()));

      // TODO: The guard should only apply for auto?
      LayoutConstraint usedCrossSize = !itemCrossSize.isBounded() ?
        LayoutConstraint.of(fitContent) :
        itemCrossSize;
      
      UnmanagedBoxFragment<?> fragmentAtCross = itemBox.layout(usedCrossSize, LayoutConstraint.AUTO);
      item.setBaseSize(fragmentAtCross.height(Measurement.CONTENT));
    }
    for (FlexItem item: items) {
      // TODO: Clamp the size
      item.setHypotheticalMainSize(item.baseSize());
    }
  }

  private static CSSValue determineUsedBasis(FlexItem item, boolean isVertical) {
    PropertyContainer properties = item.box().properties();
    CSSValue basis = properties.get(CSSProperty.FLEX_BASIS);
    if (!basis.equals(CSSValue.AUTO)) return basis;
    basis = isVertical ?
      properties.get(CSSProperty.HEIGHT) :
      properties.get(CSSProperty.WIDTH);
    if (!basis.equals(CSSValue.AUTO)) return basis;
    return FlexBasisValue.CONTENT;
  }

}
