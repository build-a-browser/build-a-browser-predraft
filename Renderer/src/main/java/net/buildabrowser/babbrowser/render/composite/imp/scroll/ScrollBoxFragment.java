package net.buildabrowser.babbrowser.render.composite.imp.scroll;

import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.paint.UnreachableBoxPainter;

public class ScrollBoxFragment extends UnmanagedBoxFragment {
  
  private final boolean hasHorizontalScroll;
  private final boolean hasVerticalScroll;
  private final UnmanagedBoxFragment innerFragment;

  public ScrollBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    boolean hasHorizontalScroll, boolean hasVerticalScroll,
    ElementBox box, UnmanagedBoxFragment innerFragment
  ) {
    super(width, height, inkWidth, inkHeight, box, new UnreachableBoxPainter());
    this.hasHorizontalScroll = hasHorizontalScroll;
    this.hasVerticalScroll = hasVerticalScroll;
    this.innerFragment = innerFragment;
  }

  public boolean hasHorizontalScroll() {
    return this.hasHorizontalScroll;
  }

  public boolean hasVerticalScroll() {
    return this.hasVerticalScroll;
  }

  public UnmanagedBoxFragment outerFragment() {
    return this;
  }

  public UnmanagedBoxFragment innerFragment() {
    return this.innerFragment;
  }

  @Override
  public String toString() {
    return "[ScrollBoxFragment pos=["
      + borderX() + ", " + borderY() + "] size=["
      + width(Measurement.CONTENT) + "x" + height(Measurement.CONTENT) + "] inkSize=["
      + inkWidth(Measurement.CONTENT) + "x" + inkHeight(Measurement.CONTENT) + "] innerFragment=["
      + innerFragment + "]]";
  }
  
}
