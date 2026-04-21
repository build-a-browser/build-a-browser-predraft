package net.buildabrowser.babbrowser.render.box.imp;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;

public class ElementBoxDimensionsImp implements ElementBoxDimensions {

  private final ElementBox box;

  private final float[] computedBorder = new float[] { 0, 0, 0, 0 };
  private final float[] computedPadding = new float[] { 0, 0, 0, 0 };
  private final float[] computedMargin = new float[] { 0, 0, 0, 0 };

  private float staticX = 0;
  private float staticY = 0;

  private float intrinsicWidth = -1;
  private float intrinsicHeight = -1;
  private float intrinsicRatio = -1;

  private int scrollX = 0;
  private int scrollY = 0;

  public ElementBoxDimensionsImp(ElementBox box) {
    this.box = box;
  }

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
    return box.layout(LayoutConstraint.MIN_CONTENT, LayoutConstraint.AUTO).contentWidth();
  }

  @Override
  public float preferredWidthConstraint() {
    return box.layout(LayoutConstraint.MAX_CONTENT, LayoutConstraint.AUTO).contentWidth();
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
    return computedBorder[2] + computedBorder[3] + computedPadding[2] + computedPadding[3];
  }

  @Override
  public float decorHeight() {
    return computedBorder[0] + computedBorder[1] + computedPadding[0] + computedPadding[1];
  }

  @Override
  public int scrollX() {
    return this.scrollX;
  }

  @Override
  public int scrollY() {
    return this.scrollY;
  }

  @Override
  public void setScroll(int scrollX, int scrollY) {
    this.scrollX = scrollX;
    this.scrollY = scrollY;
  }
  
}
