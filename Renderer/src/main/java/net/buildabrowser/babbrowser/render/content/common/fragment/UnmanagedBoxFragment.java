package net.buildabrowser.babbrowser.render.content.common.fragment;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.paint.BoxPainter;

public class UnmanagedBoxFragment extends BoxFragment {
  
  public UnmanagedBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    ElementBox box, BoxPainter painter
  ) {
    super(
      width, height,
      inkWidth, inkHeight,
      box, painter);
  }

  public UnmanagedBoxFragment(
    float width, float height,
    ElementBox box
  ) {
    super(width, height, box);
  }

  public UnmanagedBoxFragment(float x, float y, float width, float height, ElementBox box) {
    this(width, height, box);
    setPos(x, y);
  }

  @Override
  public String toString() {
    return "[UnmanagedBoxFragment pos=["
      + borderX() + ", " + borderY() + "] size=["
      + width(Measurement.CONTENT) + "x" + height(Measurement.CONTENT) + "] inkSize=["
      + inkWidth(Measurement.CONTENT) + "x" + inkHeight(Measurement.CONTENT) + "]]";
  }

}