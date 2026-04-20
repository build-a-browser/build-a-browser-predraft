package net.buildabrowser.babbrowser.render.composite.imp.scroll;

import static net.buildabrowser.babbrowser.render.composite.imp.scroll.ScrollLayoutUtil.GUTTER_WIDTH;

import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.paint.PaintCanvas;

public class ScrollContentPainter {

  private static final int GUTTER_BG_COLOR = 0xFFFFFFFF;
  private static final int GUTTER_FG_COLOR = 0xFF888888;

  public static void paint(ScrollBoxFragment scrollContext, PaintCanvas canvas) {
    UnmanagedBoxFragment outerFragment = scrollContext.outerFragment();

    if (
      scrollContext.hasHorizontalScroll()
      && scrollContext.hasVerticalScroll()
    ) {
      paintHorizontalScroller(scrollContext, canvas, Math.max(0, outerFragment.contentWidth() - GUTTER_WIDTH));
      paintVerticalScroller(scrollContext, canvas, Math.max(0, outerFragment.contentHeight() - GUTTER_WIDTH));
    } else if (scrollContext.hasHorizontalScroll()) {
      paintHorizontalScroller(scrollContext, canvas, outerFragment.contentWidth());
    } else if (scrollContext.hasVerticalScroll()) {
      paintVerticalScroller(scrollContext, canvas, outerFragment.contentHeight());
    }
  }

  private static void paintHorizontalScroller(ScrollBoxFragment scrollContext, PaintCanvas canvas, float scrollerSpace) {
    UnmanagedBoxFragment outerFragment = scrollContext.outerFragment();
    UnmanagedBoxFragment innerFragment = scrollContext.innerFragment();
    float startY = outerFragment.contentY() + outerFragment.contentHeight() - GUTTER_WIDTH;
    float startX = outerFragment.contentX();
    float scrollerStart = 0 / innerFragment.inkWidth(Measurement.CONTENT) * scrollerSpace;
    float scrollerWidth = scrollerSpace / innerFragment.inkWidth(Measurement.CONTENT) * scrollerSpace;
    canvas.alterPaint(c -> c.setColor(GUTTER_BG_COLOR));
    canvas.drawBox(startX, startY, outerFragment.contentWidth(), GUTTER_WIDTH);
    canvas.alterPaint(c -> c.setColor(GUTTER_FG_COLOR));
    canvas.drawBox(startX, startY + scrollerStart, scrollerWidth, GUTTER_WIDTH);
  }

  private static void paintVerticalScroller(ScrollBoxFragment scrollContext, PaintCanvas canvas, float scrollerSpace) {
    UnmanagedBoxFragment outerFragment = scrollContext.outerFragment();
    UnmanagedBoxFragment innerFragment = scrollContext.innerFragment();
    float startX = outerFragment.contentX() + outerFragment.contentWidth() - GUTTER_WIDTH;
    float startY = outerFragment.contentY();
    float scrollerStart = 0 / innerFragment.inkHeight(Measurement.CONTENT) * scrollerSpace;
    float scrollerHeight = scrollerSpace / innerFragment.inkHeight(Measurement.CONTENT) * scrollerSpace;
    canvas.alterPaint(c -> c.setColor(GUTTER_BG_COLOR));
    canvas.drawBox(startX, startY, GUTTER_WIDTH, outerFragment.contentHeight());
    canvas.alterPaint(c -> c.setColor(GUTTER_FG_COLOR));
    canvas.drawBox(startX, startY + scrollerStart, GUTTER_WIDTH, scrollerHeight);
  }

  public static void paintBackground(ScrollBoxFragment scrollBox, PaintCanvas canvas) {
    // TODO: Proper background painting
    BoxFragment innerFragment = scrollBox.innerFragment();
    canvas.clip(
      0, 0,
      innerFragment.contentWidth(), innerFragment.contentHeight());
    innerFragment.painter().paintBackground(innerFragment, canvas);
    canvas.unclip();
  }

  public static void paintForeground(ScrollBoxFragment scrollBox, PaintCanvas canvas) {
    BoxFragment innerFragment = scrollBox.innerFragment();
    canvas.clip(
      0, 0,
      innerFragment.contentWidth(), innerFragment.contentHeight());
    innerFragment.painter().paint(innerFragment, canvas);
    canvas.unclip();
  }
  
}
