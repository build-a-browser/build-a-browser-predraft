package net.buildabrowser.babbrowser.renderer.content.grid.imp;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridMinMaxValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeValue;
import net.buildabrowser.babbrowser.renderer.content.generic.GenericItem;
import net.buildabrowser.babbrowser.renderer.content.grid.GridTrack;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class GridTrackImp implements GridTrack {

  private CSSValue minTrackSizingValue = CSSValue.AUTO;
  private CSSValue maxTrackSizingValue = CSSValue.AUTO;
  private CSSValue sizeValue = CSSValue.AUTO;

  private LayoutConstraint baseSize;
  private LayoutConstraint growthLimit;

  private boolean frozen;
  private boolean isInfinitelyGrowable;
  private float itemIncrease;
  private float plannedIncrease;

  private float position;
  private boolean hasPlannedIncrease;

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

    if (sizeValue.equals(CSSValue.AUTO)) {
      this.minTrackSizingValue = CSSValue.AUTO;
      this.maxTrackSizingValue = SizeValue.MAX_CONTENT;
    } else if (sizeValue instanceof GridMinMaxValue minMaxValue) {
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
      newMaxTrackSizingValue instanceof SizeValue.FitContent
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
    this.hasPlannedIncrease = true;
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
  public boolean hasPlannedIncrease() {
    return this.hasPlannedIncrease;
  }

  @Override
  public float plannedIncrease() {
    float increase = this.plannedIncrease;
    this.plannedIncrease = 0;
    this.hasPlannedIncrease = false;
    return increase;
  }

  @Override
  public boolean isInfinitelyGrowable() {
    return this.isInfinitelyGrowable;
  }

  @Override
  public void setInfinitelyGrowable(boolean isInfinitelyGrowable) {
    this.isInfinitelyGrowable = isInfinitelyGrowable;
  }

  @Override
  public float position() {
    return this.position;
  }

  @Override
  public void setPosition(float position) {
    this.position = position;
  }

  // Justify

  @Override
  public float mainSize() {
    assert baseSize.isBounded();
    return baseSize.value();
  }

  @Override
  public void setMainPos(float startPos) {
    this.position = startPos;
  }

  @Override
  public LayoutConstraint firstMargin(LayoutConstraint parentSize) {
    return LayoutConstraint.of(0);
  }

  @Override
  public LayoutConstraint secondMargin(LayoutConstraint parentSize) {
    return LayoutConstraint.of(0);
  }

  // GenericTrack

  @Override
  public List<GenericItem> genericItems() {
    throw new UnsupportedOperationException("Not implemented!");
  }

  @Override
  public float crossSize() {
    assert baseSize.isBounded();
    return baseSize.value();
  }

  @Override
  public void setCrossPos(float startPos) {
    this.position = startPos;
  }
  
}
