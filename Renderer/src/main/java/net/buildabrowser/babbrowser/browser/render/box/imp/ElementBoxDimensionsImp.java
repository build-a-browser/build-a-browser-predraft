package net.buildabrowser.babbrowser.browser.render.box.imp;

import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;

public class ElementBoxDimensionsImp implements ElementBoxDimensions {
  
  private int computedWidth = 0;
  private int computedHeight = 0;

  private int[] computedBorder = new int[] { 0, 0, 0, 0 };
  private int[] computedPadding = new int[] { 0, 0, 0, 0 };
  private int[] computedMargin = new int[] { 0, 0, 0, 0 };

  private int preferredMinWidthConstraint = 0;
  private int preferredWidthConstraint = 0;

  private int intrinsicWidth = -1;
  private int intrinsicHeight = -1;
  private float intrinsicRatio = -1;

  @Override
  public void setComputedSize(int w, int h) {
    this.computedWidth = w;
    this.computedHeight = h;
  }

  @Override
  public int getComputedWidth() {
    return this.computedWidth;
  }

  @Override
  public int getComputedHeight() {
    return this.computedHeight;
  }

  @Override
  public void setComputedBorder(int t, int b, int l, int r) {
    computedBorder[0] = t;
    computedBorder[1] = b;
    computedBorder[2] = l;
    computedBorder[3] = r;
  }

  @Override
  public int[] getComputedBorder() {
    return computedBorder;
  }

  @Override
  public void setComputedPadding(int t, int b, int l, int r) {
    computedPadding[0] = t;
    computedPadding[1] = b;
    computedPadding[2] = l;
    computedPadding[3] = r;
  }

  @Override
  public int[] getComputedPadding() {
    return computedPadding;
  }

  @Override
  public void setComputedVerticalMargin(int t, int b) {
    computedMargin[0] = t;
    computedMargin[1] = b;
  }

  @Override
  public void setComputedHorizontalMargin(int l , int r) {
    computedMargin[2] = l;
    computedMargin[3] = r;
  }

  @Override
  public int[] getComputedMargin() {
    return this.computedMargin;
  }

  @Override
  public void setPreferredMinWidthConstraint(int w) {
    this.preferredMinWidthConstraint = w;
  }

  @Override
  public void setPreferredWidthConstraint(int w) {
    this.preferredWidthConstraint = w;
  }

  @Override
  public int preferredMinWidthConstraint() {
    return this.preferredMinWidthConstraint;
  }

  @Override
  public int preferredWidthConstraint() {
    return this.preferredWidthConstraint;
  }

  @Override
  public void setIntrinsicWidth(int width) {
    this.intrinsicWidth = width;
  }

  @Override
  public void setInstrinsicHeight(int height) {
    this.intrinsicHeight = height;
  }

  @Override
  public void setIntrinsicRatio(float ratio) {
    this.intrinsicRatio = ratio;
  }

  @Override
  public int intrinsicWidth() {
    return this.intrinsicWidth;
  }

  @Override
  public int intrinsicHeight() {
    return this.intrinsicHeight;
  }

  @Override
  public float intrinsicRatio() {
    return this.intrinsicRatio;
  }
  
}
