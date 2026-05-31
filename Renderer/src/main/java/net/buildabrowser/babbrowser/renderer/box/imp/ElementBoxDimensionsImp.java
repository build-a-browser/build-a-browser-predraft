package net.buildabrowser.babbrowser.renderer.box.imp;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;

public class ElementBoxDimensionsImp implements ElementBoxDimensions {

  private static final float[] ZERO_SIZE = new float[4];

  private final ElementBox box;

  private float[] computedBorder;
  private float[] computedPadding;
  private float[] computedMargin;

  private float staticX = 0;
  private float staticY = 0;

  private float intrinsicWidth = -1;
  private float intrinsicHeight = -1;
  private float intrinsicRatio = -1;

  public ElementBoxDimensionsImp(ElementBox box) {
    this.box = box;
  }

  @Override
  public void setComputedBorder(float t, float b, float l, float r) {
    if (computedBorder == null) {
      if (
        t == 0 && b == 0 && l == 0 && r == 0
      ) return;
      computedBorder = new float[4];
    }

    computedBorder[0] = t;
    computedBorder[1] = b;
    computedBorder[2] = l;
    computedBorder[3] = r;
  }

  @Override
  public float[] getComputedBorder() {
    if (computedBorder == null) return ZERO_SIZE;
    return computedBorder;
  }

  @Override
  public void setComputedPadding(float t, float b, float l, float r) {
    if (computedPadding == null) {
      if (
        t == 0 && b == 0 && l == 0 && r == 0
      ) return;
      computedPadding = new float[4];
    }

    computedPadding[0] = t;
    computedPadding[1] = b;
    computedPadding[2] = l;
    computedPadding[3] = r;
  }

  @Override
  public float[] getComputedPadding() {
    if (computedPadding == null) return ZERO_SIZE;
    return computedPadding;
  }

  @Override
  public void setComputedVerticalMargin(float t, float b) {
    if (computedMargin == null) {
      if (t == 0 && b == 0) return;
      computedMargin = new float[4];
    }

    computedMargin[0] = t;
    computedMargin[1] = b;
  }

  @Override
  public void setComputedHorizontalMargin(float l , float r) {
    if (computedMargin == null) {
      if (l == 0 && r == 0) return;
      computedMargin = new float[4];
    }
    
    computedMargin[2] = l;
    computedMargin[3] = r;
  }

  @Override
  public float[] getComputedMargin() {
    if (computedMargin == null) return ZERO_SIZE;
    return this.computedMargin;
  }

  @Override
  public void setStaticPosition(float staticX, float staticY) {
    this.staticX = staticX;
    this.staticY = staticY;
  }

  @Override
  public float staticX() {
    return this.staticX;
  }

  @Override
  public float staticY() {
    return this.staticY;
  }

  @Override
  public float preferredMinWidthConstraint() {
    return box.layout(LayoutConstraint.MIN_CONTENT, LayoutConstraint.AUTO).width(Measurement.CONTENT);
  }

  @Override
  public float preferredWidthConstraint() {
    return box.layout(LayoutConstraint.MAX_CONTENT, LayoutConstraint.AUTO).width(Measurement.CONTENT);
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

  @Override
  public float decorWidth() {
    float border = computedBorder == null ? 0 : computedBorder[2] + computedBorder[3];
    float padding = computedPadding == null ? 0 : computedPadding[2] + computedPadding[3];
    return border + padding;
  }

  @Override
  public float decorHeight() {
    float border = computedBorder == null ? 0 : computedBorder[0] + computedBorder[1];
    float padding = computedPadding == null ? 0 : computedPadding[0] + computedPadding[1];
    return border + padding;
  }
  
}
