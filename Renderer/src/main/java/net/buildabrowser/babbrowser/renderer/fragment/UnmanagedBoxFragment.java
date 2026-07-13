package net.buildabrowser.babbrowser.renderer.fragment;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;

public abstract class UnmanagedBoxFragment<T extends UnmanagedBoxFragment<T>> extends BoxFragment<T> {
  
  public UnmanagedBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox box
  ) {
    super(
      width, height,
      inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      box);
  }

  @Override
  public String toString() {
    return "[UnmanagedBoxFragment pos=["
      + posX(Measurement.BORDER) + ", " + posY(Measurement.BORDER) + "] size=["
      + width(Measurement.CONTENT) + "x" + height(Measurement.CONTENT) + "] inkSize=["
      + inkWidth(Measurement.CONTENT) + "x" + inkHeight(Measurement.CONTENT) + "]]";
  }

}