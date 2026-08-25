package net.buildabrowser.babbrowser.renderer.fragment;

import net.buildabrowser.babbrowser.renderer.box.ElementBox;

public abstract class UnmanagedBoxFragment<T extends UnmanagedBoxFragment<T>> extends BoxFragment<T> {

  private BoxFragment<?> fragments;
  
  public UnmanagedBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    float firstBaseline, float lastBaseline,
    ElementBox box, BoxFragment<?> fragments
  ) {
    super(
      width, height,
      inkWidth, inkHeight,
      firstBaseline, lastBaseline,
      box);
    this.fragments = fragments;
  }

  public BoxFragment<?> innerFragment() {
    return this.fragments;
  }

  @Override
  public String toString() {
    return "[UnmanagedBoxFragment pos=["
      + posX(Measurement.BORDER) + ", " + posY(Measurement.BORDER) + "] size=["
      + width(Measurement.CONTENT) + "x" + height(Measurement.CONTENT) + "] inkSize=["
      + inkWidth(Measurement.CONTENT) + "x" + inkHeight(Measurement.CONTENT) + "]]";
  }

}