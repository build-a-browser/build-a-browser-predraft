package net.buildabrowser.babbrowser.renderer.content.scroll;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.content.common.paint.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollMath.ScrollMathResult;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;

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
    // TODO: Is this right?
    ElementBackgroundPainter.paintBackground(canvas, fragment);
  }

  public void paintScrollbars(ScrollBoxFragment scrollBoxFragment, PaintCanvas canvas) {
    if (
      scrollBoxFragment.hasHorizontalScroll()
      && scrollBoxFragment.hasVerticalScroll()
    ) {
      canvas.withPaint(
        p -> p.setColor(GUTTER_BG_COLOR),
        c -> c.drawBox(
          scrollBoxFragment.width(Measurement.CONTENT) - GUTTER_WIDTH,
          scrollBoxFragment.height(Measurement.CONTENT) - GUTTER_WIDTH,
          GUTTER_WIDTH, GUTTER_WIDTH));
    }
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

    canvas.withPaint(
      p -> p.setColor(GUTTER_BG_COLOR),
      c -> c.drawBox(
        scrollInfo.trackX(), scrollInfo.trackY(),
        scrollInfo.trackSize(), GUTTER_WIDTH));

    int scrollerColor = determineScrollColor(scrollState);
    canvas.withPaint(
      p -> p.setColor(scrollerColor),
      c -> c.drawBox(
        scrollInfo.trackX() + scrollInfo.scrollerPos(), scrollInfo.trackY(),
        scrollInfo.scrollerSize(), GUTTER_WIDTH));
  }

  private static void paintVerticalScroller(ScrollBoxFragment scrollBoxFragment, PaintCanvas canvas) {
    ScrollMathResult scrollInfo = scrollBoxFragment.verticalScrollInfo();
    ScrollBarState scrollState = scrollBoxFragment.box().verticalScrollState();

    canvas.withPaint(
      p -> p.setColor(GUTTER_BG_COLOR),
      c -> c.drawBox(scrollInfo.trackX(), scrollInfo.trackY(), GUTTER_WIDTH, scrollInfo.trackSize()));

    int scrollerColor = determineScrollColor(scrollState);
    canvas.withPaint(
      p -> p.setColor(scrollerColor),
      c -> c.drawBox(
        scrollInfo.trackX(), scrollInfo.trackY() + scrollInfo.scrollerPos(),
        GUTTER_WIDTH, scrollInfo.scrollerSize()));
  }

  private static int determineScrollColor(ScrollBarState scrollState) {
    return
      scrollState.active() ? GUTTER_FG_COLOR_ACTIVE :
      scrollState.hovered() ? GUTTER_FG_COLOR_HOVERED : GUTTER_FG_COLOR;
  }
  
}
