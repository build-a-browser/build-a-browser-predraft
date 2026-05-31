package net.buildabrowser.babbrowser.renderer.box;

import net.buildabrowser.babbrowser.renderer.box.imp.ElementBoxDimensionsImp;

public interface ElementBoxDimensions {

  void setComputedBorder(float t, float b, float l, float r);

  float[] getComputedBorder();

  void setComputedPadding(float t, float b, float l, float r);

  float[] getComputedPadding();

  void setComputedVerticalMargin(float t, float b);

  void setComputedHorizontalMargin(float l , float r);

  float[] getComputedMargin();

  void setStaticPosition(float staticX, float staticY);

  float staticX();

  float staticY();

  float preferredMinWidthConstraint();

  float preferredWidthConstraint();

  void setIntrinsicWidth(float width);

  void setInstrinsicHeight(float height);

  void setIntrinsicRatio(float ratio);
  
  float intrinsicWidth();

  float intrinsicHeight();

  float intrinsicRatio();

  float decorWidth();

  float decorHeight();

  static ElementBoxDimensions create(ElementBox box) {
    return new ElementBoxDimensionsImp(box);
  }
  
}
