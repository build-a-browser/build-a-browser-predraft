package net.buildabrowser.babbrowser.renderer.content.flexbox;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexGrowValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexShrinkValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.content.common.SizingHeightUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingUtil;
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

  public void computeMinMaxSizes(LayoutConstraint refMainSize, boolean isVertical) {
    this.isVertical = isVertical;

    PropertyContainer properties = itemBox.properties();
    CSSValue minSizeValue = isVertical ?
      properties.get(CSSProperty.MIN_HEIGHT) :
      properties.get(CSSProperty.MIN_WIDTH);
    LayoutConstraint minMainSizeC = isVertical ?
      SizingHeightUtil.evaluateAdjustedHeightSize(refMainSize, itemBox, minSizeValue) :
      SizingWidthUtil.evaluateWidthSize(refMainSize, itemBox, minSizeValue);
    if (minMainSizeC.isBounded()) {
      this.minMainSize = minMainSizeC.value();
    }
    
    CSSValue maxSizeValue = isVertical ?
      properties.get(CSSProperty.MAX_HEIGHT) :
      properties.get(CSSProperty.MAX_WIDTH);
    LayoutConstraint maxMainSizeC = isVertical ?
      SizingHeightUtil.evaluateAdjustedHeightSize(refMainSize, itemBox, maxSizeValue) :
      SizingWidthUtil.evaluateWidthSize(refMainSize, itemBox, maxSizeValue);
    if (maxMainSizeC.isBounded()) {
      this.maxMainSize = maxMainSizeC.value();
    }
  }

  public void setBaseSize(float baseSize) {
    this.baseSize = baseSize;
    this.mainSize = baseSize;
  }

  public float baseSize() {
    return this.baseSize;
  }

  public void setHypotheticalMainSize(float hypotheticalMainSize) {
    if (maxMainSize != null) {
      hypotheticalMainSize = Math.min(hypotheticalMainSize, maxMainSize);
    }
    hypotheticalMainSize = Math.max(hypotheticalMainSize, minMainSize);
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

  public float mainSize() {
    return this.mainSize;
  }

  public float minMainSize() {
    return this.minMainSize;
  }

  public Float maxMainSize() {
    return this.maxMainSize;
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
  public void setMainPos(float startPos, boolean isVertical) {
    if (isVertical) {
      boxFragment.setPos(0, startPos);
    } else {
      boxFragment.setPos(startPos, 0);
    }
  }

  @Override
  public void setCrossPos(float itemCrossPos, boolean isVertical) {
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
    boolean isVertical, LayoutConstraint parentSize
  ) {
    PropertyContainer properties = itemBox.properties();
    CSSValue relevantValue = isVertical ?
      properties.get(CSSProperty.MARGIN_TOP) :
      properties.get(CSSProperty.MARGIN_LEFT);
    return SizingUtil.evaluateBaseSize(
      itemBox.layoutContext(), parentSize, relevantValue);
  }

  @Override
  public LayoutConstraint secondMargin(
    boolean isVertical, LayoutConstraint parentSize
  ) {
    PropertyContainer properties = itemBox.properties();
    CSSValue relevantValue = isVertical ?
      properties.get(CSSProperty.MARGIN_BOTTOM) :
      properties.get(CSSProperty.MARGIN_RIGHT);
    return SizingUtil.evaluateBaseSize(
      itemBox.layoutContext(), parentSize, relevantValue);
  }

  @Override
  public float decorMainSize(boolean isVertical) {
    return this.mainSize + (isVertical ?
      itemBox.dimensions().decorHeight() :
      itemBox.dimensions().decorWidth());
  }

  public void setFragment(UnmanagedBoxFragment<?> boxFragment) {
    this.boxFragment = boxFragment;
  }

  @Override
  public UnmanagedBoxFragment<?> fragment() {
    return this.boxFragment;
  }

  public float outerSize(float innerSize) {
    ElementBoxDimensions dimensions = itemBox.dimensions();
    float[] margin = dimensions.getComputedMargin();
    float[] border = dimensions.getComputedBorder();
    float[] padding = dimensions.getComputedPadding();
    return isVertical ?
      innerSize + margin[0] + margin[1] + border[0] + border[1] + padding[0] + padding[1] :
      innerSize + margin[2] + margin[3] + border[2] + border[3] + padding[2] + padding[3];
  }

}
