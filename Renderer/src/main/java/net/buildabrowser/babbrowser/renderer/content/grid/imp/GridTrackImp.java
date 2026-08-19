package net.buildabrowser.babbrowser.renderer.content.grid.imp;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridMinMaxValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue;
import net.buildabrowser.babbrowser.renderer.content.grid.GridTrack;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class GridTrackImp implements GridTrack {

  private CSSValue minTrackSizingValue = CSSValue.AUTO;
  private CSSValue maxTrackSizingValue = CSSValue.AUTO;
  private CSSValue sizeValue = CSSValue.AUTO;

  private LayoutConstraint baseSize;
  private LayoutConstraint growthLimit;

  private boolean frozen;
  private float itemIncrease;
  private float plannedIncrease;

  private float position;

  @Override
  public CSSValue minTrackSizingFunction() {
    return this.minTrackSizingValue;
  }

  @Override
  public CSSValue maxTrackSizingFunction() {
    return this.maxTrackSizingValue;
  }

  @Override
  public void setSizeValue(CSSValue sizeValue) {
    this.sizeValue = sizeValue;

    CSSValue newMinTrackSizingValue = sizeValue;
    CSSValue newMaxTrackSizingValue = sizeValue;

    if (sizeValue instanceof GridMinMaxValue minMaxValue) {
      newMinTrackSizingValue = minMaxValue.min();
      newMaxTrackSizingValue = minMaxValue.max();
    }

    if (
      (
        newMinTrackSizingValue instanceof LengthValue lengthValue
        && LengthType.FR.equals(lengthValue.dimension()))
      || newMinTrackSizingValue instanceof SizeValue.FitContent
    ) {
      newMinTrackSizingValue = CSSValue.AUTO;
    }

    if (
      newMaxTrackSizingValue.equals(CSSValue.AUTO)
      || newMaxTrackSizingValue instanceof SizeValue.FitContent
    ) {
      newMaxTrackSizingValue = SizeValue.MAX_CONTENT;
    }

    this.minTrackSizingValue = newMinTrackSizingValue;
    this.maxTrackSizingValue = newMaxTrackSizingValue;
  }

  @Override
  public LayoutConstraint baseSize() {
    return this.baseSize;
  }

  @Override
  public boolean frozen() {
    return this.frozen;
  }

  @Override
  public void setBaseSize(LayoutConstraint baseSize) {
    this.baseSize = baseSize;
  }

  @Override
  public LayoutConstraint growthLimit() {
    return this.growthLimit;
  }

  @Override
  public void setGrowthLimit(LayoutConstraint growthLimit) {
    this.growthLimit = growthLimit;
  }

  @Override
  public CSSValue _sizeValue() {
    return this.sizeValue;
  }

  @Override
  public void setFrozen(boolean frozen) {
    this.frozen = frozen;
  }

  @Override
  public void increaseItemIncurredIncrease(float increase) {
    this.itemIncrease += increase;
  }

  @Override
  public float itemIncurredIncrease() {
    return this.itemIncrease;
  }

  @Override
  public void finalizeItemIncurredIncrease() {
    this.plannedIncrease = Math.max(this.plannedIncrease, this.itemIncrease);
    this.itemIncrease = 0;
  }

  @Override
  public float plannedIncrease() {
    return this.plannedIncrease;
  }

  @Override
  public float position() {
    return this.position;
  }

  @Override
  public void setPosition(float position) {
    this.position = position;
  }
  
}
