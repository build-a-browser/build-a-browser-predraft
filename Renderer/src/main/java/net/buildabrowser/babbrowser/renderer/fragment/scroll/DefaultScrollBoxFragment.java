package net.buildabrowser.babbrowser.renderer.fragment.scroll;

import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.scroll.ScrollEventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.painters.scroll.ScrollBoxPainter;

public class DefaultScrollBoxFragment extends ScrollBoxFragment {

  private static final ScrollBoxPainter SCROLL_BOX_PAINTER = new ScrollBoxPainter();
  private static final ScrollEventHandler SCROLL_EVENT_HANDLER = new ScrollEventHandler();

  public DefaultScrollBoxFragment(
    float width, float height,
    float inkWidth, float inkHeight,
    boolean hasHorizontalScroll,
    boolean hasVerticalScroll,
    ScrollBox box, UnmanagedBoxFragment<?> innerFragment
  ) {
    super(
      width, height, inkWidth, inkHeight,
      hasHorizontalScroll, hasVerticalScroll,
      box, innerFragment);
  }

  @Override
  public ScrollBoxPainter painter() {
    return SCROLL_BOX_PAINTER;
  }

  @Override
  protected EventHandler<ScrollBoxFragment> eventHandler() {
    return SCROLL_EVENT_HANDLER;
  }
  
}
