package net.buildabrowser.babbrowser.renderer.box;

public interface MutableElementBoxDimensions extends ElementBoxDimensions {

  void setComputedBorder(float t, float b, float l, float r);

  void setComputedPadding(float t, float b, float l, float r);

  void setComputedVerticalMargin(float t, float b);

  void setComputedHorizontalMargin(float l , float r);

  void setStaticPosition(float staticX, float staticY);

  void setIntrinsicWidth(float width);

  void setIntrinsicHeight(float height);

  void setIntrinsicRatio(float ratio);
  
}
