package net.buildabrowser.babbrowser.browser.render.box.imp;

import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;

public class ElementBoxDimensionsImp implements ElementBoxDimensions {

  private float[] computedBorder = new float[] { 0, 0, 0, 0 };
  private float[] computedPadding = new float[] { 0, 0, 0, 0 };
  private float[] computedMargin = new float[] { 0, 0, 0, 0 };

  private float preferredMinWidthConstraint = 0;
  private float preferredWidthConstraint = 0;

  private float intrinsicWidth = -1;
  private float intrinsicHeight = -1;
  private float intrinsicRatio = -1;

  @Override
  public void setComputedBorder(float t, float b, float l, float r) {
    computedBorder[0] = t;
    computedBorder[1] = b;
    computedBorder[2] = l;
    computedBorder[3] = r;
  }

  @Override
  public float[] getComputedBorder() {
    return computedBorder;
  }

  @Override
  public void setComputedPadding(float t, float b, float l, float r) {
    computedPadding[0] = t;
    computedPadding[1] = b;
    computedPadding[2] = l;
    computedPadding[3] = r;
  }

  @Override
  public float[] getComputedPadding() {
    return computedPadding;
  }

  @Override
  public void setComputedVerticalMargin(float t, float b) {
    computedMargin[0] = t;
    computedMargin[1] = b;
  }

  @Override
  public void setComputedHorizontalMargin(float l , float r) {
    computedMargin[2] = l;
    computedMargin[3] = r;
  }

  @Override
  public float[] getComputedMargin() {
    return this.computedMargin;
  }

  @Override
  public void setPreferredMinWidthConstraint(float w) {
    this.preferredMinWidthConstraint = w;
  }

  @Override
  public void setPreferredWidthConstraint(float w) {
    this.preferredWidthConstraint = w;
  }

  @Override
  public float preferredMinWidthConstraint() {
    return this.preferredMinWidthConstraint;
  }

  @Override
  public float preferredWidthConstraint() {
    return this.preferredWidthConstraint;
  }

  @Override
  public void setIntrinsicWidth(float width) {
    this.intrinsicWidth = width;
  }

  @Override
  public void setInstrinsicHeight(float height) {
    this.intrinsicHeight = height;
  }

  @Override
  public void setIntrinsicRatio(float ratio) {
    this.intrinsicRatio = ratio;
  }

  @Override
  public float intrinsicWidth() {
    return this.intrinsicWidth;
  }

  @Override
  public float intrinsicHeight() {
    return this.intrinsicHeight;
  }

  @Override
  public float intrinsicRatio() {
    return this.intrinsicRatio;
  }
  
}
