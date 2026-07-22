package net.buildabrowser.babbrowser.renderer.content.scroll;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.imp.ElementBoxImp;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;

public class ScrollBox extends ElementBoxImp {

  private final ScrollBoxContent SCROLL_BOX_CONTENT = new ScrollBoxContent();

  private final ScrollBarState horizontalScrollState;
  private final ScrollBarState verticalScrollState;

  private int scrollLeft = 0;
  private int scrollTop = 0;

  public ScrollBox(ElementContext context, Box parentBox, BoxLevel boxLevel) {
    super(context, parentBox, boxLevel);
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

  public void setRawScrollLeft(int newScrollX) {
    this.scrollLeft = newScrollX;
    context().invalidate(InvalidationLevel.PAINT);
  }

  public void setRawScrollTop(int newScrollY) {
    this.scrollTop = newScrollY;
    context().invalidate(InvalidationLevel.PAINT);
  }

  public int rawScrollLeft() {
    return this.scrollLeft;
  }

  public int rawScrollTop() {
    return this.scrollTop;
  }
  
  public ScrollBarState horizontalScrollState() {
    return this.horizontalScrollState;
  }
  
  public ScrollBarState verticalScrollState() {
    return this.verticalScrollState;
  }
  
}
