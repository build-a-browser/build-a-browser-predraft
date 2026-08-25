package net.buildabrowser.babbrowser.renderer.content.scroll;

import static net.buildabrowser.babbrowser.renderer.paint.painters.scroll.ScrollBoxPainter.GUTTER_WIDTH;
import static net.buildabrowser.babbrowser.renderer.paint.painters.scroll.ScrollBoxPainter.MIN_SCROLLBAR_HEIGHT;

import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.ScrollBoxFragment;

public final class ScrollMath {
  
  private ScrollMath() {}

  public static ScrollMathResult computeHorizontalScroller(
    ScrollBoxFragment scrollBoxFragment
  ) {
    BoxFragment<?> innerFragment = scrollBoxFragment.innerFragment();

    float trackHeightReduction = scrollBoxFragment.hasHorizontalScroll() && scrollBoxFragment.hasVerticalScroll() ? 16 : 0;
    float trackSize = Math.max(scrollBoxFragment.width(Measurement.PADDING) - trackHeightReduction, 0);
    float contentOffsetX = scrollBoxFragment.posX(Measurement.PADDING) - scrollBoxFragment.posX(Measurement.BORDER);
    float contentOffsetY = scrollBoxFragment.posY(Measurement.PADDING) - scrollBoxFragment.posY(Measurement.BORDER);
    float trackX = contentOffsetX;
    // TODO: Subtract bottom border
    float trackY = contentOffsetY + scrollBoxFragment.height(Measurement.BORDER) - GUTTER_WIDTH;
    float scrollerSize = trackSize / innerFragment.inkWidth(Measurement.PADDING) * trackSize;

    float reducedTrackHeight = trackSize;
    if (
      scrollerSize < MIN_SCROLLBAR_HEIGHT
      && MIN_SCROLLBAR_HEIGHT < trackSize
    ) {
      float extraHeight = MIN_SCROLLBAR_HEIGHT - scrollerSize;
      scrollerSize = MIN_SCROLLBAR_HEIGHT;
      reducedTrackHeight -= extraHeight;
    }
    reducedTrackHeight = Math.max(reducedTrackHeight, 0);
    float scrollerPos = scrollBoxFragment.scrollX() / innerFragment.inkWidth(Measurement.PADDING) * reducedTrackHeight;

    return new ScrollMathResult(trackX, trackY, scrollerPos, scrollerSize, trackSize);
  }

  public static ScrollMathResult computeVerticalScroller(
    ScrollBoxFragment scrollBoxFragment
  ) {
    BoxFragment<?> innerFragment = scrollBoxFragment.innerFragment();

    float trackHeightReduction = scrollBoxFragment.hasHorizontalScroll() && scrollBoxFragment.hasVerticalScroll() ? 16 : 0;
    float trackSize = Math.max(scrollBoxFragment.height(Measurement.PADDING) - trackHeightReduction, 0);
    float contentOffsetX = scrollBoxFragment.posX(Measurement.PADDING) - scrollBoxFragment.posX(Measurement.BORDER);
    float contentOffsetY = scrollBoxFragment.posY(Measurement.PADDING) - scrollBoxFragment.posY(Measurement.BORDER);
    float trackX = contentOffsetX + scrollBoxFragment.width(Measurement.PADDING) - GUTTER_WIDTH;
    float trackY = contentOffsetY;
    float scrollerSize = trackSize / innerFragment.inkHeight(Measurement.PADDING) * trackSize;

    float reducedTrackHeight = trackSize;
    if (
      scrollerSize < MIN_SCROLLBAR_HEIGHT
      && MIN_SCROLLBAR_HEIGHT < trackSize
    ) {
      float extraHeight = MIN_SCROLLBAR_HEIGHT - scrollerSize;
      scrollerSize = MIN_SCROLLBAR_HEIGHT;
      reducedTrackHeight -= extraHeight;
    }
    reducedTrackHeight = Math.max(reducedTrackHeight, 0);
    // TODO: Subtract right border
    float scrollerPos = scrollBoxFragment.scrollY() / innerFragment.inkHeight(Measurement.PADDING) * reducedTrackHeight;

    return new ScrollMathResult(trackX, trackY, scrollerPos, scrollerSize, trackSize);
  }

  public static record ScrollMathResult(
    float trackX, float trackY, float scrollerPos, float scrollerSize, float trackSize
  ) {}

}
