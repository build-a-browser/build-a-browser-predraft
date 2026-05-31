package net.buildabrowser.babbrowser.renderer.content.flexbox;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexGrowValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexShrinkValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.content.common.SizingHeightUtil;
import net.buildabrowser.babbrowser.renderer.content.common.SizingWidthUtil;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class FlexItem {
  
  private final ElementBox itemBox;
  private final float growFactor;
  private final float shrinkFactor;

  private UnmanagedBoxFragment boxFragment;
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
    this.growFactor = ((FlexGrowValue) itemBox.activeStyles()
      .getProperty(CSSProperty.FLEX_GROW)).value().floatValue();
    this.shrinkFactor = ((FlexShrinkValue) itemBox.activeStyles()
      .getProperty(CSSProperty.FLEX_SHRINK)).value().floatValue();
  }

  public ElementBox box() {
    return this.itemBox;
  }

  public void computeMinMaxSizes(LayoutConstraint refMainSize, boolean isVertical) {
    this.isVertical = isVertical;

    ActiveStyles activeStyles = itemBox.activeStyles();
    CSSValue minSizeValue = isVertical ?
      activeStyles.getProperty(CSSProperty.MIN_HEIGHT) :
      activeStyles.getProperty(CSSProperty.MIN_WIDTH);
    LayoutConstraint minMainSizeC = isVertical ?
      SizingHeightUtil.evaluateAdjustedHeightSize(refMainSize, itemBox, minSizeValue) :
      SizingWidthUtil.evaluateAdjustedWidthSize(refMainSize, itemBox, minSizeValue);
    if (minMainSizeC.isBounded()) {
      this.minMainSize = minMainSizeC.value();
    }
    
    CSSValue maxSizeValue = isVertical ?
      activeStyles.getProperty(CSSProperty.MAX_HEIGHT) :
      activeStyles.getProperty(CSSProperty.MAX_WIDTH);
    LayoutConstraint maxMainSizeC = isVertical ?
      SizingHeightUtil.evaluateAdjustedHeightSize(refMainSize, itemBox, maxSizeValue) :
      SizingWidthUtil.evaluateAdjustedWidthSize(refMainSize, itemBox, maxSizeValue);
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

  public float usedCrossSize() {
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

  public void setFragment(UnmanagedBoxFragment boxFragment) {
    this.boxFragment = boxFragment;
  }

  public UnmanagedBoxFragment fragment() {
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
