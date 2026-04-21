package net.buildabrowser.babbrowser.render.composite.imp.scroll;


import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.event.EventHandler;
import net.buildabrowser.babbrowser.render.paint.UnreachableBoxPainter;

public class ScrollBoxFragment extends UnmanagedBoxFragment {
  
  private final boolean hasHorizontalScroll;
  private final boolean hasVerticalScroll;
  private final UnmanagedBoxFragment innerFragment;
  private final EventHandler eventHandler;

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
    this.eventHandler = new ScrollEventHandler(this);
  }

  @Override
  public EventHandler eventHandler() {
    return this.eventHandler;
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

  public void scroll(int scrollX, int scrollY) {
    ElementBoxDimensions dimensions = box().dimensions();
    int newScrollX = (int) Math.ceil(Math.clamp(
      dimensions.scrollX() + scrollX,
      0, this.inkWidth(Measurement.CONTENT) - this.width(Measurement.CONTENT)));
    int newScrollY = (int) Math.ceil(Math.clamp(
      dimensions.scrollY() + scrollY,
      0, this.inkHeight(Measurement.CONTENT) - this.height(Measurement.CONTENT)));
    dimensions.setScroll(newScrollX, newScrollY);
    box().element().invalidate(InvalidationLevel.PAINT);
  }

  public int scrollX() {
    if (!hasHorizontalScroll) return 0;
    return (int) Math.min(
      box().dimensions().scrollX(),
      this.inkWidth(Measurement.CONTENT) - this.width(Measurement.CONTENT));
  }

  public int scrollY() {
    if (!hasVerticalScroll) return 0;
    return (int) Math.min(
      box().dimensions().scrollY(),
      this.inkHeight(Measurement.CONTENT) - this.height(Measurement.CONTENT));
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
