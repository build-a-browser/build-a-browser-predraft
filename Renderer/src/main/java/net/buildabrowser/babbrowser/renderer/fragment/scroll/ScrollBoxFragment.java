package net.buildabrowser.babbrowser.renderer.fragment.scroll;


import static net.buildabrowser.babbrowser.common.util.CompatUtil.mathClamp;

import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollBox;
import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollMath;
import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollMath.ScrollMathResult;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.painters.scroll.ScrollBoxPainter;

public abstract class ScrollBoxFragment extends UnmanagedBoxFragment<ScrollBoxFragment> {

  private final boolean hasHorizontalScroll;
  private final boolean hasVerticalScroll;
  private final UnmanagedBoxFragment<?> innerFragment;

  public ScrollBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    boolean hasHorizontalScroll, boolean hasVerticalScroll,
    ScrollBox box, UnmanagedBoxFragment<?> innerFragment
  ) {
    super(
      width, height, inkWidth, inkHeight,
      innerFragment.firstBaseline(Measurement.CONTENT),
      innerFragment.lastBaseline(Measurement.CONTENT),
      box);
    this.hasHorizontalScroll = hasHorizontalScroll;
    this.hasVerticalScroll = hasVerticalScroll;
    this.innerFragment = innerFragment;
  }

  @Override
  public ScrollBox box() {
    return (ScrollBox) super.box();
  }

  public boolean hasHorizontalScroll() {
    return this.hasHorizontalScroll;
  }

  public boolean hasVerticalScroll() {
    return this.hasVerticalScroll;
  }

  public UnmanagedBoxFragment<?> innerFragment() {
    return this.innerFragment;
  }

  public ScrollMathResult horizontalScrollInfo() {
    return ScrollMath.computeHorizontalScroller(this);
  }

  public ScrollMathResult verticalScrollInfo() {
    return ScrollMath.computeVerticalScroller(this);
  }

  public void scroll(int scrollX, int scrollY) {
    setScrollX(box().rawScrollLeft() + scrollX);
    setScrollY(box().rawScrollTop() + scrollY);
  }

  public void setScrollX(float newScrollX) {
    box().setRawScrollLeft((int) Math.ceil(mathClamp(
      newScrollX,
      0, inkWidth(Measurement.CONTENT) - width(Measurement.CONTENT))));
  }

  public void setScrollY(float newScrollY) {
    box().setRawScrollTop((int) Math.ceil(mathClamp(
      newScrollY,
      0, inkHeight(Measurement.CONTENT) - height(Measurement.CONTENT))));
  }

  public int scrollX() {
    if (!hasHorizontalScroll()) return 0;
    return (int) Math.min(
      box().rawScrollLeft(),
      inkWidth(Measurement.CONTENT) - width(Measurement.CONTENT));
  }

  public int scrollY() {
    if (!hasVerticalScroll()) return 0;
    return (int) Math.min(
      box().rawScrollTop(),
      inkHeight(Measurement.CONTENT) - height(Measurement.CONTENT));
  }

  public abstract ScrollBoxPainter painter();

  @Override
  public String toString() {
    return "[ScrollBoxFragment pos=["
      + posX(Measurement.BORDER) + ", " + posY(Measurement.BORDER) + "] size=["
      + width(Measurement.CONTENT) + "x" + height(Measurement.CONTENT) + "] inkSize=["
      + inkWidth(Measurement.CONTENT) + "x" + inkHeight(Measurement.CONTENT) + "] innerFragment=["
      + innerFragment + "]]";
  }
  
}
