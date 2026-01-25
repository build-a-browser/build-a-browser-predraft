package net.buildabrowser.babbrowser.browser.render.box;

import net.buildabrowser.babbrowser.browser.render.box.imp.ElementBoxDimensionsImp;

public interface ElementBoxDimensions {

  void setComputedSize(int w, int h);

  int getComputedWidth();

  int getComputedHeight();

  void setComputedBorder(int t, int b, int l, int r);

  int[] getComputedBorder();

  void setComputedPadding(int t, int b, int l, int r);

  int[] getComputedPadding();

  void setComputedVerticalMargin(int t, int b);

  void setComputedHorizontalMargin(int l , int r);

  int[] getComputedMargin();

  void setComputedInsets(int t, int b, int l, int r);

  int[] getComputedInsets();

  void setPreferredMinWidthConstraint(int w);

  void setPreferredWidthConstraint(int w);

  int preferredMinWidthConstraint();

  int preferredWidthConstraint();

  void setIntrinsicWidth(int width);

  void setInstrinsicHeight(int height);

  void setIntrinsicRatio(float ratio);
  
  int intrinsicWidth();

  int intrinsicHeight();

  float intrinsicRatio();

  static ElementBoxDimensions create() {
    return new ElementBoxDimensionsImp();
  }
  
}
