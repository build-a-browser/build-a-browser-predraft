package net.buildabrowser.babbrowser.browser.render.box;

import net.buildabrowser.babbrowser.browser.render.box.imp.ElementBoxDimensionsImp;

public interface ElementBoxDimensions {

  void setComputedBorder(float t, float b, float l, float r);

  float[] getComputedBorder();

  void setComputedPadding(float t, float b, float l, float r);

  float[] getComputedPadding();

  void setComputedVerticalMargin(float t, float b);

  void setComputedHorizontalMargin(float l , float r);

  float[] getComputedMargin();

  void setPreferredMinWidthConstraint(float w);

  void setPreferredWidthConstraint(float w);

  float preferredMinWidthConstraint();

  float preferredWidthConstraint();

  void setIntrinsicWidth(float width);

  void setInstrinsicHeight(float height);

  void setIntrinsicRatio(float ratio);
  
  float intrinsicWidth();

  float intrinsicHeight();

  float intrinsicRatio();

  static ElementBoxDimensions create() {
    return new ElementBoxDimensionsImp();
  }
  
}
