package net.buildabrowser.babbrowser.render.content.scroll;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.scroll.ScrollMath.ScrollMathResult;
import net.buildabrowser.babbrowser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.render.paint.PaintCanvas;

public class ScrollContentPainter implements BoxPainter {

  public static final int GUTTER_WIDTH = 16;
  public static final int MIN_SCROLLBAR_HEIGHT = 16;

  private static final int GUTTER_BG_COLOR = 0xFFFFFFFF;
  private static final int GUTTER_FG_COLOR = 0xFF888888;
  private static final int GUTTER_FG_COLOR_HOVERED = 0xFF666666;
  private static final int GUTTER_FG_COLOR_ACTIVE = 0xFF444444;

  // Composite Layer already adjusts vp for scrolling and the inner fragment
  @Override
  public void paint(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
    ScrollBoxFragment scrollBoxFragment = (ScrollBoxFragment) fragment;
    BoxFragment innerFragment = scrollBoxFragment.innerFragment();
    innerFragment.painter().paint(innerFragment, canvas, vpIntersection);
  }

  @Override
  public void paintBackground(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
    // TOD: I think border and stuff might be outside the scrollbox (haven't checked),
    // in which case this method should be changed accordingly
    ScrollBoxFragment scrollBoxFragment = (ScrollBoxFragment) fragment;
    BoxFragment innerFragment = scrollBoxFragment.innerFragment();
    innerFragment.painter().paintBackground(innerFragment, canvas, vpIntersection);
  }

  public void paintScrollbars(ScrollBoxFragment scrollBoxFragment, PaintCanvas canvas) {
    if (scrollBoxFragment.hasHorizontalScroll()) {
      paintHorizontalScroller(scrollBoxFragment, canvas);
    }
    if (scrollBoxFragment.hasVerticalScroll()) {
      paintVerticalScroller(scrollBoxFragment, canvas);
    }
  }

  private static void paintHorizontalScroller(ScrollBoxFragment scrollBoxFragment, PaintCanvas canvas) {
    ScrollMathResult scrollInfo = scrollBoxFragment.horizontalScrollInfo();
    ScrollBarState scrollState = scrollBoxFragment.box().horizontalScrollState();
    canvas.alterPaint(c -> c.setColor(GUTTER_BG_COLOR));
    canvas.drawBox(scrollInfo.trackX(), scrollInfo.trackY(), scrollInfo.trackSize(), GUTTER_WIDTH);
    int scrollerColor = determineScrollColor(scrollState);
    canvas.alterPaint(c -> c.setColor(scrollerColor));
    canvas.drawBox(scrollInfo.trackX() + scrollInfo.scrollerPos(), scrollInfo.trackY(),
    scrollInfo.scrollerSize(), GUTTER_WIDTH);
  }

  private static void paintVerticalScroller(ScrollBoxFragment scrollBoxFragment, PaintCanvas canvas) {
    ScrollMathResult scrollInfo = scrollBoxFragment.verticalScrollInfo();
    ScrollBarState scrollState = scrollBoxFragment.box().verticalScrollState();
    canvas.alterPaint(c -> c.setColor(GUTTER_BG_COLOR));
    canvas.drawBox(scrollInfo.trackX(), scrollInfo.trackY(), GUTTER_WIDTH, scrollInfo.trackSize());
    int scrollerColor = determineScrollColor(scrollState);
    canvas.alterPaint(c -> c.setColor(scrollerColor));
    canvas.drawBox(
      scrollInfo.trackX(), scrollInfo.trackY() + scrollInfo.scrollerPos(),
      GUTTER_WIDTH, scrollInfo.scrollerSize());
  }

  private static int determineScrollColor(ScrollBarState scrollState) {
    return
      scrollState.active() ? GUTTER_FG_COLOR_ACTIVE :
      scrollState.hovered() ? GUTTER_FG_COLOR_HOVERED : GUTTER_FG_COLOR;
  }
  
}
