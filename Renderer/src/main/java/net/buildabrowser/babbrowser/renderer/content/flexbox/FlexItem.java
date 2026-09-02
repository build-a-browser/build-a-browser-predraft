package net.buildabrowser.babbrowser.renderer.content.flexbox;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexGrowValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexShrinkValue;
import net.buildabrowser.babbrowser.renderer.box.EBDimensionsUtil;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.content.common.SizingHeightUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingWidthUtil;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericItem;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericJustifyContentItem;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class FlexItem implements GenericItem, GenericJustifyContentItem {
  
  private final ElementBox itemBox;
  private final float growFactor;
  private final float shrinkFactor;

  private UnmanagedBoxFragment<?> boxFragment;
  private float baseSize;
  private float hypotheticalMainSize;
  private float hypotheticalCrossSize;
  private float mainSize; // Also represents targetMainSize too
  private float minMainSize;
  private Float maxMainSize;
  private float usedCrossSize;
  private Float innerPreferredSize;
  private boolean isFrozen;
  private boolean isVertical;

  public FlexItem(ElementBox itemBox) {
    this.itemBox = itemBox;
    this.growFactor = ((FlexGrowValue) itemBox.properties()
      .get(CSSProperty.FLEX_GROW)).value().floatValue();
    this.shrinkFactor = ((FlexShrinkValue) itemBox.properties()
      .get(CSSProperty.FLEX_SHRINK)).value().floatValue();
  }

  @Override
  public ElementBox box() {
    return this.itemBox;
  }

  public void computeMinMaxSizes(
    LayoutConstraint refMainSize,
    LayoutConstraint refCrossSize,
    boolean isVertical
  ) {
    this.isVertical = isVertical;
    PropertyContainer properties = itemBox.properties();

    this.innerPreferredSize = determinePreferredSize(
      properties, CSSProperty.WIDTH, CSSProperty.HEIGHT,
      refMainSize, isVertical);

    this.maxMainSize = determinePreferredSize(
      properties, CSSProperty.MAX_WIDTH, CSSProperty.MAX_HEIGHT,
      refMainSize, isVertical);

    Float minMainSize = determinePreferredSize(
      properties, CSSProperty.MIN_WIDTH, CSSProperty.MIN_HEIGHT,
      refMainSize, isVertical);
    if (minMainSize == null) {
      float autoMin = automaticMinSize(refCrossSize);
      if (this.maxMainSize != null) {
        autoMin = Math.min(autoMin, this.maxMainSize);
      }
      this.minMainSize = autoMin;
    } else {
      this.minMainSize = minMainSize;
    }
  }

  public void setBaseSize(float baseSize) {
    float outerBase = outerSize(baseSize);
    this.baseSize = outerBase;
    this.mainSize = outerBase;
  }

  public float baseSize() {
    return this.baseSize;
  }

  public void setHypotheticalMainSize(float hypotheticalMainSize) {
    Float maxOuterMainSize = maxMainSize();
    if (maxOuterMainSize != null) {
      hypotheticalMainSize = Math.min(hypotheticalMainSize, maxOuterMainSize);
    }
    hypotheticalMainSize = Math.max(hypotheticalMainSize, minMainSize());
    this.hypotheticalMainSize = hypotheticalMainSize;
  }

  public float hypotheticalMainSize() {
    return this.hypotheticalMainSize;
  }

  public void setHypotheticalCrossSize(float hypotheticalCrossSize) {
    this.hypotheticalCrossSize = hypotheticalCrossSize;
  }

  public float hypotheticalCrossSize() {
    return this.hypotheticalCrossSize;
  }

  public float innerMainSize() {
    return innerSize(this.mainSize);
  }

  @Override
  public float mainSize() {
    return this.mainSize;
  }

  public float minMainSize() {
    return outerSize(this.minMainSize);
  }

  public Float maxMainSize() {
    if (this.maxMainSize == null) return null;
    return outerSize(this.maxMainSize);
  }

  public void setTargetMainSize(float targetMainSize) {
    this.mainSize = targetMainSize;
  }

  public float crossSize() {
    return this.usedCrossSize;
  }

  public void setCrossSize(float usedCrossSize) {
    this.usedCrossSize = usedCrossSize;
  }

  public void freeze() {
    this.isFrozen = true;
  }
  
  public boolean isFrozen() {
    return this.isFrozen;
  }

  public float growFactor() {
    return this.growFactor;
  }

  public float shrinkFactor() {
    return this.shrinkFactor;
  }

  @Override
  public void setMainPos(float startPos) {
    if (isVertical) {
      boxFragment.setPos(0, startPos);
    } else {
      boxFragment.setPos(startPos, 0);
    }
  }

  @Override
  public void setCrossPos(float itemCrossPos) {
    if (isVertical) {
      boxFragment.setPos(
        itemCrossPos,
        boxFragment.posY(Measurement.BORDER));
    } else {
      boxFragment.setPos(
        boxFragment.posX(Measurement.BORDER),
        itemCrossPos);
    }
  }

  @Override
  public LayoutConstraint firstMargin(
    LayoutConstraint parentSize
  ) {
    PropertyContainer properties = itemBox.properties();
    CSSValue relevantValue = isVertical ?
      properties.get(CSSProperty.MARGIN_TOP) :
      properties.get(CSSProperty.MARGIN_LEFT);
    if (relevantValue.equals(CSSValue.AUTO)) {
      return LayoutConstraint.AUTO;
    }

    float[] margin = itemBox.dimensions().getComputedMargin();
    return LayoutConstraint.of(isVertical ? margin[0] : margin[2]);
  }

  @Override
  public LayoutConstraint secondMargin(
    LayoutConstraint parentSize
  ) {
    PropertyContainer properties = itemBox.properties();
    CSSValue relevantValue = isVertical ?
      properties.get(CSSProperty.MARGIN_BOTTOM) :
      properties.get(CSSProperty.MARGIN_RIGHT);
    if (relevantValue.equals(CSSValue.AUTO)) {
      return LayoutConstraint.AUTO;
    }
    float[] margin = itemBox.dimensions().getComputedMargin();
    return LayoutConstraint.of(isVertical ? margin[1] : margin[3]);
  }

  public float mainMargin() {
    ElementBoxDimensions dimensions = itemBox.dimensions();
    float[] margin = dimensions.getComputedMargin();
    return isVertical ?
      margin[0] + margin[1] :
      margin[2] + margin[3];
  }

  public void setFragment(UnmanagedBoxFragment<?> boxFragment) {
    this.boxFragment = boxFragment;
  }

  @Override
  public UnmanagedBoxFragment<?> fragment() {
    return this.boxFragment;
  }

  public float outerSize(float innerSize) {
    return innerSize + decorSize(false);
  }

  public float innerSize(float outerSize) {
    return Math.max(0, outerSize - decorSize(false));
  }

  public float minContentContribution(
    LayoutConstraint crossSize
  ) {
    float size = innerPreferredSize != null ?
      outerSize(innerPreferredSize) :
      (isVertical ?
        itemBox.layout(crossSize, LayoutConstraint.MIN_CONTENT).height(Measurement.MARGIN) :
        outerSize(EBDimensionsUtil.preferredMinWidthConstraint(itemBox)));
    return clampContribution(size);
  }

  public float maxContentContribution(
    LayoutConstraint crossSize
  ) {
    float size = innerPreferredSize != null ?
      outerSize(innerPreferredSize) :
      (isVertical ?
        itemBox.layout(crossSize, LayoutConstraint.MAX_CONTENT).height(Measurement.MARGIN) :
        outerSize(EBDimensionsUtil.preferredWidthConstraint(itemBox)));
    return clampContribution(size);
  }

  private Float determinePreferredSize(
    PropertyContainer properties,
    CSSProperty horizProp,
    CSSProperty vertProp,
    LayoutConstraint refMainSize,
    boolean isVertical
  ) {
    CSSProperty refProperty = isVertical ? vertProp : horizProp;
    CSSValue value = properties.get(refProperty);
    LayoutConstraint determinedConstraint = isVertical ?
      SizingHeightUtil.evaluateAdjustedHeightSize(refMainSize, itemBox, refProperty, value) :
      SizingWidthUtil.evaluateWidthSize(refMainSize, itemBox, refProperty, value);
    return determinedConstraint.isBounded() ?
      determinedConstraint.value() : null;
  }

  private float automaticMinSize(LayoutConstraint crossSize) {
    float determinedMinWidth = isVertical ?
      itemBox.layout(crossSize, LayoutConstraint.AUTO).height(Measurement.CONTENT) :
      EBDimensionsUtil.preferredMinWidthConstraint(itemBox);
    Float transferredSizeSuggestion = transferredSizeSuggestion(crossSize);
    if (transferredSizeSuggestion != null) {
      determinedMinWidth = itemBox.isReplaced() ?
        Math.min(determinedMinWidth, transferredSizeSuggestion) :
        Math.max(determinedMinWidth, transferredSizeSuggestion);
    }
    if (this.innerPreferredSize != null) {
      determinedMinWidth = Math.min(
        determinedMinWidth, this.innerPreferredSize);
    }
    
    return determinedMinWidth;
  }

  private Float transferredSizeSuggestion(
    LayoutConstraint crossSize
  ) {
    if (!crossSize.isBounded()) return null;

    float ratio = itemBox.dimensions().intrinsicRatio();
    if (ratio == -1) return null;

    return isVertical ?
      crossSize.value() / ratio :
      ratio * crossSize.value();
  }

  private float decorSize(boolean includeMargin) {
    ElementBoxDimensions dimensions = itemBox.dimensions();
    float[] margin = dimensions.getComputedMargin();
    float[] border = dimensions.getComputedBorder();
    float[] padding = dimensions.getComputedPadding();
    float totalMargin = !includeMargin ? 0 : isVertical ?
      margin[0] + margin[1] :
      margin[2] + margin[3];
    float subDecor = isVertical ?
      totalMargin + border[0] + border[1] + padding[0] + padding[1] :
      totalMargin + border[2] + border[3] + padding[2] + padding[3];
    return subDecor;
  }

  private float clampContribution(float size) {
    if (growFactor() == 0) {
      size = Math.min(size, baseSize());
    }

    if (shrinkFactor() == 0) {
      size = Math.max(size, baseSize());
    }

    if (maxMainSize != null) {
      size = Math.min(size, outerSize(maxMainSize));
    }
    size = Math.max(size, outerSize(minMainSize));
    return size;
  }

}
