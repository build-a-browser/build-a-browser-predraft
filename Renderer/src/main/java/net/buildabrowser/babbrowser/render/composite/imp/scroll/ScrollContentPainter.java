package net.buildabrowser.babbrowser.render.composite.imp.scroll;

import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.paint.PaintCanvas;

public class ScrollContentPainter {

  public static final int GUTTER_WIDTH = 16;

  private static final int GUTTER_BG_COLOR = 0xFFFFFFFF;
  private static final int GUTTER_FG_COLOR = 0xFF888888;
  private static final int MIN_SCROLLBAR_HEIGHT = 16;

  public static void paint(ScrollBoxFragment scrollContext, PaintCanvas canvas) {
    UnmanagedBoxFragment outerFragment = scrollContext.outerFragment();

    if (
      scrollContext.hasHorizontalScroll()
      && scrollContext.hasVerticalScroll()
    ) {
      paintHorizontalScroller(scrollContext, canvas, Math.max(0, outerFragment.width(Measurement.CONTENT) - GUTTER_WIDTH));
      paintVerticalScroller(scrollContext, canvas, Math.max(0, outerFragment.height(Measurement.CONTENT) - GUTTER_WIDTH));
    } else if (scrollContext.hasHorizontalScroll()) {
      paintHorizontalScroller(scrollContext, canvas, outerFragment.width(Measurement.CONTENT));
    } else if (scrollContext.hasVerticalScroll()) {
      paintVerticalScroller(scrollContext, canvas, outerFragment.height(Measurement.CONTENT));
    }
  }

  private static void paintHorizontalScroller(ScrollBoxFragment scrollContext, PaintCanvas canvas, float scrollerSpace) {
    UnmanagedBoxFragment outerFragment = scrollContext.outerFragment();
    UnmanagedBoxFragment innerFragment = scrollContext.innerFragment();
    float startY = outerFragment.contentY() + outerFragment.height(Measurement.CONTENT) - GUTTER_WIDTH;
    float startX = outerFragment.contentX();
    float scrollerWidth = scrollerSpace / innerFragment.inkWidth(Measurement.CONTENT) * scrollerSpace;
    if (
      scrollerWidth < MIN_SCROLLBAR_HEIGHT
      && MIN_SCROLLBAR_HEIGHT < scrollerSpace
    ) {
      float extraWidth = MIN_SCROLLBAR_HEIGHT - scrollerWidth;
      scrollerWidth += extraWidth;
      scrollerSpace -= extraWidth;
    }
    float scrollerStart = scrollContext.scrollX() / innerFragment.inkWidth(Measurement.CONTENT) * scrollerSpace;
    canvas.alterPaint(c -> c.setColor(GUTTER_BG_COLOR));
    canvas.drawBox(startX, startY, outerFragment.width(Measurement.CONTENT), GUTTER_WIDTH);
    canvas.alterPaint(c -> c.setColor(GUTTER_FG_COLOR));
    canvas.drawBox(startX, startY + scrollerStart, scrollerWidth, GUTTER_WIDTH);
  }

  private static void paintVerticalScroller(ScrollBoxFragment scrollContext, PaintCanvas canvas, float scrollerSpace) {
    UnmanagedBoxFragment outerFragment = scrollContext.outerFragment();
    UnmanagedBoxFragment innerFragment = scrollContext.innerFragment();
    float startX = outerFragment.contentX() + outerFragment.width(Measurement.CONTENT) - GUTTER_WIDTH;
    float startY = outerFragment.contentY();
    float scrollerHeight = scrollerSpace / innerFragment.inkHeight(Measurement.CONTENT) * scrollerSpace;
    if (
      scrollerHeight < MIN_SCROLLBAR_HEIGHT
      && MIN_SCROLLBAR_HEIGHT < scrollerSpace
    ) {
      float extraHeight = MIN_SCROLLBAR_HEIGHT - scrollerHeight;
      scrollerHeight += extraHeight;
      scrollerSpace -= extraHeight;
    }
    float scrollerStart = scrollContext.scrollY() / innerFragment.inkHeight(Measurement.CONTENT) * scrollerSpace;
    canvas.alterPaint(c -> c.setColor(GUTTER_BG_COLOR));
    canvas.drawBox(startX, startY, GUTTER_WIDTH, outerFragment.height(Measurement.CONTENT));
    canvas.alterPaint(c -> c.setColor(GUTTER_FG_COLOR));
    canvas.drawBox(startX, startY + scrollerStart, GUTTER_WIDTH, scrollerHeight);
  }

  public static void paintBackground(ScrollBoxFragment scrollBox, PaintCanvas canvas) {
    // TODO: Proper background painting
    BoxFragment innerFragment = scrollBox.innerFragment();
    canvas.clip(
      scrollBox.scrollX(), scrollBox.scrollY(),
      innerFragment.width(Measurement.CONTENT), innerFragment.height(Measurement.CONTENT));
    innerFragment.painter().paintBackground(innerFragment, canvas);
    canvas.unclip();
  }

  public static void paintForeground(ScrollBoxFragment scrollBox, PaintCanvas canvas) {
    BoxFragment innerFragment = scrollBox.innerFragment();
    canvas.clip(
      scrollBox.scrollX(), scrollBox.scrollY(),
      innerFragment.width(Measurement.CONTENT), innerFragment.height(Measurement.CONTENT));
    innerFragment.painter().paint(innerFragment, canvas);
    canvas.unclip();
  }
  
}
