package net.buildabrowser.babbrowser.render.composite.imp.scroll;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.render.box.Box;
import net.buildabrowser.babbrowser.render.box.BoxContent;
import net.buildabrowser.babbrowser.render.box.imp.ElementBoxImp;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;

public class ScrollBox extends ElementBoxImp {

  private final ScrollBoxContent SCROLL_BOX_CONTENT = new ScrollBoxContent(this);

  private final ScrollBarState horizontalScrollState;
  private final ScrollBarState verticalScrollState;

  public ScrollBox(HTMLElement element, Box parentBox, BoxLevel boxLevel) {
    super(element, parentBox, boxLevel);
    this.horizontalScrollState = new ScrollBarState();
    this.verticalScrollState = new ScrollBarState();
  }

  @Override
  public BoxContent content() {
    return SCROLL_BOX_CONTENT;
  }

  public void scroll(int scrollX, int scrollY) {
    setScrollX(dimensions().scrollX() + scrollX);
    setScrollY(dimensions().scrollY() + scrollY);
  }

  public void setScrollX(float newScrollX) {
    if (scrollFragment() == null) return;
    int newScrollYBounded = (int) Math.ceil(Math.clamp(
      newScrollX,
      0, scrollFragment().inkWidth(Measurement.CONTENT) - scrollFragment().width(Measurement.CONTENT)));
    dimensions().setScroll(newScrollYBounded, dimensions().scrollY());
    element().invalidate(InvalidationLevel.PAINT);
  }

  public void setScrollY(float newScrollY) {
    if (scrollFragment() == null) return;
    int newScrollYBounded = (int) Math.ceil(Math.clamp(
      newScrollY,
      0, scrollFragment().inkHeight(Measurement.CONTENT) - scrollFragment().height(Measurement.CONTENT)));
    dimensions().setScroll(dimensions().scrollX(), newScrollYBounded);
    element().invalidate(InvalidationLevel.PAINT);
  }

  public int scrollX() {
    if (scrollFragment() == null) return 0;
    if (!scrollFragment().hasHorizontalScroll()) return 0;
    return (int) Math.min(
      dimensions().scrollX(),
      scrollFragment().inkWidth(Measurement.CONTENT) - scrollFragment().width(Measurement.CONTENT));
  }

  public int scrollY() {
    if (scrollFragment() == null) return 0;
    if (!scrollFragment().hasVerticalScroll()) return 0;
    return (int) Math.min(
      dimensions().scrollY(),
      scrollFragment().inkHeight(Measurement.CONTENT) - scrollFragment().height(Measurement.CONTENT));
  }
  
  public ScrollBarState horizontalScrollState() {
    return this.horizontalScrollState;
  }
  
  public ScrollBarState verticalScrollState() {
    return this.verticalScrollState;
  }

  ScrollBoxFragment scrollFragment() {
    return (ScrollBoxFragment) positioningFragment();
  }
  
}
