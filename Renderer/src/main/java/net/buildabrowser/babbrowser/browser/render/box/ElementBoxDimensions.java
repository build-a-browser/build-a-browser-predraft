package net.buildabrowser.babbrowser.browser.render.box;

import net.buildabrowser.babbrowser.browser.render.box.imp.ElementBoxDimensionsImp;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;

public interface ElementBoxDimensions {

  void setComputedBorder(float t, float b, float l, float r);

  float[] getComputedBorder();

  void setComputedPadding(float t, float b, float l, float r);

  float[] getComputedPadding();

  void setComputedVerticalMargin(float t, float b);

  void setComputedHorizontalMargin(float l , float r);

  float[] getComputedMargin();

  float preferredMinWidthConstraint(LayoutContext layoutContext);

  float preferredWidthConstraint(LayoutContext layoutContext);

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
