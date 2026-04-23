package net.buildabrowser.babbrowser.render.content.scroll;


import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.scroll.ScrollMath.ScrollMathResult;
import net.buildabrowser.babbrowser.render.paint.BoxPainter;

public class ScrollBoxFragment extends UnmanagedBoxFragment {

  private static final BoxPainter SCROLL_BOX_PAINTER = new ScrollContentPainter();
  
  private final boolean hasHorizontalScroll;
  private final boolean hasVerticalScroll;
  private final UnmanagedBoxFragment innerFragment;

  public ScrollBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    boolean hasHorizontalScroll, boolean hasVerticalScroll,
    ScrollBox box, UnmanagedBoxFragment innerFragment
  ) {
    super(width, height, inkWidth, inkHeight, box, SCROLL_BOX_PAINTER);
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

  public UnmanagedBoxFragment innerFragment() {
    return this.innerFragment;
  }

  public ScrollMathResult horizontalScrollInfo() {
    return ScrollMath.computeHorizontalScroller(box());
  }

  public ScrollMathResult verticalScrollInfo() {
    return ScrollMath.computeVerticalScroller(box());
  }

  @Override
  public ScrollBox box() {
    return (ScrollBox) super.box();
  }

  @Override
  public String toString() {
    return "[ScrollBoxFragment pos=["
      + posX(Measurement.BORDER) + ", " + posY(Measurement.BORDER) + "] size=["
      + width(Measurement.CONTENT) + "x" + height(Measurement.CONTENT) + "] inkSize=["
      + inkWidth(Measurement.CONTENT) + "x" + inkHeight(Measurement.CONTENT) + "] innerFragment=["
      + innerFragment + "]]";
  }
  
}
