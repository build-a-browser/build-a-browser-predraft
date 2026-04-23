package net.buildabrowser.babbrowser.render.content.scroll;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
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

  @Override
  public void paint(BoxFragment fragment, PaintCanvas canvas) {
    ScrollBoxFragment scrollBox = (ScrollBoxFragment) fragment;
    if (scrollBox.hasHorizontalScroll()) {
      paintHorizontalScroller(scrollBox, canvas);
    }
    if (scrollBox.hasVerticalScroll()) {
      paintVerticalScroller(scrollBox, canvas);
    }
  }

  @Override
  public void paintBackground(BoxFragment fragment, PaintCanvas canvas) {
    
  }

  private static void paintHorizontalScroller(ScrollBoxFragment scrollBoxFragment, PaintCanvas canvas) {
    ScrollMathResult scrollInfo = scrollBoxFragment.horizontalScrollInfo();
    ScrollBarState scrollState = scrollBoxFragment.box().horizontalScrollState();
    canvas.alterPaint(c -> c.setColor(GUTTER_BG_COLOR));
    canvas.drawBox(scrollInfo.trackX(), scrollInfo.trackY(), scrollInfo.trackSize(), GUTTER_WIDTH);
    int scrollerColor = determineScrollColor(scrollState);
    canvas.alterPaint(c -> c.setColor(scrollerColor));
    canvas.drawBox(scrollInfo.trackX(), scrollInfo.trackY() + scrollInfo.scrollerPos(),
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

  public static void paintBackground(ScrollBoxFragment scrollBoxFragment, PaintCanvas canvas) {
    // TODO: Proper background painting
    BoxFragment innerFragment = scrollBoxFragment.innerFragment();
    canvas.clip(
      scrollBoxFragment.box().scrollX(), scrollBoxFragment.box().scrollY(),
      innerFragment.width(Measurement.CONTENT), innerFragment.height(Measurement.CONTENT));
    innerFragment.painter().paintBackground(innerFragment, canvas);
    canvas.unclip();
  }

  public static void paintForeground(ScrollBoxFragment scrollBoxFragment, PaintCanvas canvas) {
    BoxFragment innerFragment = scrollBoxFragment.innerFragment();
    canvas.clip(
      scrollBoxFragment.box().scrollX(), scrollBoxFragment.box().scrollY(),
      innerFragment.width(Measurement.CONTENT), innerFragment.height(Measurement.CONTENT));
    innerFragment.painter().paint(innerFragment, canvas);
    canvas.unclip();
  }

  private static int determineScrollColor(ScrollBarState scrollState) {
    return
      scrollState.active() ? GUTTER_FG_COLOR_ACTIVE :
      scrollState.hovered() ? GUTTER_FG_COLOR_HOVERED : GUTTER_FG_COLOR;
  }
  
}
