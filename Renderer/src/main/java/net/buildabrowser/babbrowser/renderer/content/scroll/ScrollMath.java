package net.buildabrowser.babbrowser.renderer.content.scroll;

import static net.buildabrowser.babbrowser.renderer.paint.painters.scroll.ScrollBoxPainter.GUTTER_WIDTH;
import static net.buildabrowser.babbrowser.renderer.paint.painters.scroll.ScrollBoxPainter.MIN_SCROLLBAR_HEIGHT;

import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.ScrollBoxFragment;

public final class ScrollMath {
  
  private ScrollMath() {}

  public static ScrollMathResult computeHorizontalScroller(
    ScrollBoxFragment scrollBoxFragment
  ) {
    UnmanagedBoxFragment<?> innerFragment = scrollBoxFragment.innerFragment();

    float trackHeightReduction = scrollBoxFragment.hasHorizontalScroll() && scrollBoxFragment.hasVerticalScroll() ? 16 : 0;
    float trackSize = scrollBoxFragment.width(Measurement.CONTENT) - trackHeightReduction;
    float contentOffsetX = scrollBoxFragment.posX(Measurement.CONTENT) - scrollBoxFragment.posX(Measurement.BORDER);
    float contentOffsetY = scrollBoxFragment.posY(Measurement.CONTENT) - scrollBoxFragment.posY(Measurement.BORDER);
    float trackX = contentOffsetX;
    float trackY = contentOffsetY + scrollBoxFragment.height(Measurement.CONTENT) - GUTTER_WIDTH;
    float scrollerSize = trackSize / innerFragment.inkWidth(Measurement.CONTENT) * trackSize;

    float reducedTrackHeight = trackSize;
    if (
      scrollerSize < MIN_SCROLLBAR_HEIGHT
      && MIN_SCROLLBAR_HEIGHT < trackSize
    ) {
      float extraHeight = MIN_SCROLLBAR_HEIGHT - scrollerSize;
      scrollerSize = MIN_SCROLLBAR_HEIGHT;
      reducedTrackHeight -= extraHeight;
    }
    float scrollerPos = scrollBoxFragment.scrollX() / innerFragment.inkWidth(Measurement.CONTENT) * reducedTrackHeight;

    return new ScrollMathResult(trackX, trackY, scrollerPos, scrollerSize, trackSize);
  }

  public static ScrollMathResult computeVerticalScroller(
    ScrollBoxFragment scrollBoxFragment
  ) {
    UnmanagedBoxFragment<?> innerFragment = scrollBoxFragment.innerFragment();

    float trackHeightReduction = scrollBoxFragment.hasHorizontalScroll() && scrollBoxFragment.hasVerticalScroll() ? 16 : 0;
    float trackSize = scrollBoxFragment.height(Measurement.CONTENT) - trackHeightReduction;
    float contentOffsetX = scrollBoxFragment.posX(Measurement.CONTENT) - scrollBoxFragment.posX(Measurement.BORDER);
    float contentOffsetY = scrollBoxFragment.posY(Measurement.CONTENT) - scrollBoxFragment.posY(Measurement.BORDER);
    float trackX = contentOffsetX + scrollBoxFragment.width(Measurement.CONTENT) - GUTTER_WIDTH;
    float trackY = contentOffsetY;
    float scrollerSize = trackSize / innerFragment.inkHeight(Measurement.CONTENT) * trackSize;

    float reducedTrackHeight = trackSize;
    if (
      scrollerSize < MIN_SCROLLBAR_HEIGHT
      && MIN_SCROLLBAR_HEIGHT < trackSize
    ) {
      float extraHeight = MIN_SCROLLBAR_HEIGHT - scrollerSize;
      scrollerSize = MIN_SCROLLBAR_HEIGHT;
      reducedTrackHeight -= extraHeight;
    }
    float scrollerPos = scrollBoxFragment.scrollY() / innerFragment.inkHeight(Measurement.CONTENT) * reducedTrackHeight;

    return new ScrollMathResult(trackX, trackY, scrollerPos, scrollerSize, trackSize);
  }

  public static record ScrollMathResult(
    float trackX, float trackY, float scrollerPos, float scrollerSize, float trackSize
  ) {}

}
