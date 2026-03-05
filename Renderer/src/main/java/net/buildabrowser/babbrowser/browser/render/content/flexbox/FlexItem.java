package net.buildabrowser.babbrowser.browser.render.content.flexbox;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexGrowValue;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexShrinkValue;

public class FlexItem {
  
  private final ElementBox itemBox;
  private final float growFactor;
  private final float shrinkFactor;

  private UnmanagedBoxFragment boxFragment;
  private float baseSize;
  private float hypotheticalMainSize;
  private float hypotheticalCrossSize;
  private float mainSize; // Also represents targetMainSize too
  private float usedCrossSize;
  private boolean isFrozen;

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

  public void setBaseSize(float baseSize) {
    this.baseSize = baseSize;
    this.mainSize = baseSize;
  }

  public float baseSize() {
    return this.baseSize;
  }

  public void setHypotheticalMainSize(float hypotheticalMainSize) {
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
    this.mainSize = hypotheticalMainSize;
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

}
