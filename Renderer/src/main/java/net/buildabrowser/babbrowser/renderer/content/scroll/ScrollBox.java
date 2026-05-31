package net.buildabrowser.babbrowser.renderer.content.scroll;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.imp.ElementBoxImp;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment.Measurement;

public class ScrollBox extends ElementBoxImp {

  private final ScrollBoxContent SCROLL_BOX_CONTENT = new ScrollBoxContent(this);

  private final ScrollBarState horizontalScrollState;
  private final ScrollBarState verticalScrollState;

  private ScrollBoxFragment scrollFragment;

  private int scrollX = 0;
  private int scrollY = 0;

  public ScrollBox(HTMLElement element, Box parentBox, BoxLevel boxLevel) {
    super(element, parentBox, boxLevel);
    this.horizontalScrollState = new ScrollBarState();
    this.verticalScrollState = new ScrollBarState();
  }

  @Override
  public BoxContent content() {
    return SCROLL_BOX_CONTENT;
  }

  @Override
  public boolean sharesContent(ElementBox elementBox) {
    return false;
  }

  public void scroll(int scrollX, int scrollY) {
    setScrollX(this.scrollX + scrollX);
    setScrollY(this.scrollY + scrollY);
  }

  public void setScrollX(float newScrollX) {
    if (scrollFragment == null) return;
    this.scrollX = (int) Math.ceil(Math.clamp(
      newScrollX,
      0, scrollFragment.inkWidth(Measurement.CONTENT) - scrollFragment.width(Measurement.CONTENT)));
    element().invalidate(InvalidationLevel.PAINT);
  }

  public void setScrollY(float newScrollY) {
    if (scrollFragment == null) return;
    this.scrollY = (int) Math.ceil(Math.clamp(
      newScrollY,
      0, scrollFragment.inkHeight(Measurement.CONTENT) - scrollFragment.height(Measurement.CONTENT)));
    element().invalidate(InvalidationLevel.PAINT);
  }

  public int scrollX() {
    if (scrollFragment == null) return 0;
    if (!scrollFragment.hasHorizontalScroll()) return 0;
    return (int) Math.min(
      this.scrollX,
      scrollFragment.inkWidth(Measurement.CONTENT) - scrollFragment.width(Measurement.CONTENT));
  }

  public int scrollY() {
    if (scrollFragment == null) return 0;
    if (!scrollFragment.hasVerticalScroll()) return 0;
    return (int) Math.min(
      this.scrollY,
      scrollFragment.inkHeight(Measurement.CONTENT) - scrollFragment.height(Measurement.CONTENT));
  }
  
  public ScrollBarState horizontalScrollState() {
    return this.horizontalScrollState;
  }
  
  public ScrollBarState verticalScrollState() {
    return this.verticalScrollState;
  }

  ScrollBoxFragment scrollFragment() {
    return this.scrollFragment;
  }

  void setScrollFragment(ScrollBoxFragment scrollFragment) {
    this.scrollFragment = scrollFragment;
  }
  
}
