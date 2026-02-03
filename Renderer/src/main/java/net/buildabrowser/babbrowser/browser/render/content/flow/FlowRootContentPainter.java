package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.composite.LayerUtil;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LayoutFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.TextFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.browser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;

public final class FlowRootContentPainter {

  public static BoxPainter FLOW_BLOCK_PAINTER = new FlowBlockPainter();
  public static BoxPainter FLOW_INLINE_PAINTER = new FlowInlinePainter();
 
  private FlowRootContentPainter() {}

  public static class FlowRootBoxPainter implements BoxPainter {
  
    private final FlowRootContent rootContent;

    public FlowRootBoxPainter(FlowRootContent rootContent) {
      this.rootContent = rootContent;
    }

    @Override
    public void paint(BoxFragment fragment, PaintCanvas canvas) {
      canvas.pushPaint();
      paintBlockLevelBackgrounds(canvas, rootContent.rootFragment());
      paintFloats(canvas, rootContent.floatTracker());
      paintFragment(canvas, rootContent.rootFragment());
      canvas.popPaint();
    }

    @Override
    public void paintBackground(BoxFragment fragment, PaintCanvas canvas) {
      // TODO: This triggers too early for <body>
      paintBackgroundAndAdvance(canvas, fragment);
    }

    private static void paintBlockLevelBackgrounds(PaintCanvas canvas, ManagedBoxFragment fragment) {
      for (LayoutFragment childFragment: fragment.fragments()) {
        if (LayerUtil.startsLayer(childFragment)) continue;

        canvas.pushPaint();
        canvas.alterPaint(paint -> paint.incOffset(childFragment.borderX(), childFragment.borderY()));
        switch (childFragment) {
          case ManagedBoxFragment managedFragment:
            paintBackgroundAndAdvance(canvas, managedFragment);
            paintBlockLevelBackgrounds(canvas, managedFragment);
            break;
          case UnmanagedBoxFragment unmanagedFragment:
            paintBackgroundAndAdvance(canvas, unmanagedFragment);
            break;
          default:
            break;
        }
        canvas.popPaint();
      }
    }

    public static void paintFloats(PaintCanvas canvas, FloatTracker floatTracker) {
      for (LayoutFragment childFragment: floatTracker.allFloats()) {
        if (LayerUtil.startsLayer(childFragment)) continue;

        canvas.pushPaint();
        canvas.alterPaint(paint -> paint.incOffset(childFragment.borderX(), childFragment.borderY()));
        paintBackgroundAndAdvance(canvas, (BoxFragment) childFragment);
        paintFragment(canvas, childFragment);
        canvas.popPaint();
      }
    }

  }

  private static class FlowBlockPainter implements BoxPainter {

    @Override
    public void paint(BoxFragment fragment, PaintCanvas canvas) {
      paintManagedBoxFragment(canvas, (ManagedBoxFragment) fragment);
    }

    @Override
    public void paintBackground(BoxFragment fragment, PaintCanvas canvas) {
      paintBackgroundAndAdvance(canvas, fragment);
    }

  }

  private static class FlowInlinePainter implements BoxPainter {

    @Override
    public void paint(BoxFragment fragment, PaintCanvas canvas) {
      paintInlineManagedBoxFragment(canvas, (ManagedBoxFragment) fragment);
    }

    @Override
    public void paintBackground(BoxFragment fragment, PaintCanvas canvas) {
      paintBackgroundAndAdvance(canvas, fragment);
    }

  }

  public static void paintFragment(PaintCanvas canvas, LayoutFragment fragment) {
    switch (fragment) {
      case ManagedBoxFragment boxFragment -> paintManagedBoxFragment(canvas, boxFragment);
      case UnmanagedBoxFragment boxFragment -> boxFragment.painter().paint(boxFragment, canvas);
      case LineBoxFragment lineboxFragment -> paintLineBoxFragment(canvas, lineboxFragment);
      default -> throw new UnsupportedOperationException("Unrecognized Fragment Type!");
    }
  }

  private static void paintManagedBoxFragment(PaintCanvas canvas, ManagedBoxFragment fragment) {
    ElementBox parentBox = fragment.box();
    for (LayoutFragment childFragment: fragment.fragments()) {
      if (LayerUtil.startsLayer(childFragment)) continue;

      canvas.pushPaint();
      canvas.alterPaint(paint -> paint.incOffset(childFragment.contentX(), childFragment.contentY()));
      canvas.alterPaint(paint -> paint.setColor(parentBox.activeStyles().textColor()));
      paintFragment(canvas, childFragment);
      canvas.popPaint();
    }
  }

  // TODO: Unify this with above? Inline is offseting by border (then child adjusts), block-level by content
  private static void paintInlineFragment(PaintCanvas canvas, LayoutFragment fragment) {
    switch (fragment) {
      case ManagedBoxFragment boxFragment -> paintInlineManagedBoxFragment(canvas, boxFragment);
      case UnmanagedBoxFragment boxFragment -> paintInlineUnmanagedBoxFragment(canvas, boxFragment);
      case TextFragment textFragment -> paintTextFragment(canvas, textFragment);
      default -> throw new UnsupportedOperationException("Unrecognized Fragment Type!");
    }
  }

  private static void paintInlineManagedBoxFragment(PaintCanvas canvas, ManagedBoxFragment fragment) {
    paintBackgroundAndAdvance(canvas, fragment);

    ElementBox parentBox = fragment.box();
    for (LayoutFragment childFragment: fragment.fragments()) {
      if (LayerUtil.startsLayer(childFragment)) continue;

      canvas.pushPaint();
      canvas.alterPaint(paint -> paint.incOffset(childFragment.contentX(), childFragment.contentY()));
      canvas.alterPaint(paint -> paint.setColor(parentBox.activeStyles().textColor()));
      paintInlineFragment(canvas, childFragment);
      canvas.popPaint();
    }
  }

  private static void paintInlineUnmanagedBoxFragment(PaintCanvas canvas, UnmanagedBoxFragment fragment) {
    paintBackgroundAndAdvance(canvas, fragment);
    fragment.painter().paint(fragment, canvas);
  }

  private static void paintTextFragment(PaintCanvas canvas, TextFragment textFragment) {
    canvas.drawText(0, 0, textFragment.text());
  }

  private static void paintLineBoxFragment(PaintCanvas canvas, LineBoxFragment lineboxFragment) {
    for (LayoutFragment childFragment: lineboxFragment.fragments()) {
      if (LayerUtil.startsLayer(childFragment)) continue;

      canvas.pushPaint();
      canvas.alterPaint(paint -> paint.incOffset(childFragment.borderX(), childFragment.borderY()));
      paintInlineFragment(canvas, childFragment);
      canvas.popPaint();
    }
  }

  private static void paintBackgroundAndAdvance(PaintCanvas canvas, BoxFragment fragment) {
    canvas.alterPaint(paint -> paint.setColor(fragment.box().activeStyles().backgroundColor()));
    canvas.drawBox(0, 0, fragment.borderWidth(), fragment.borderHeight());

    // Might need changed when adding tables, but usually background is followed by border,
    // so it is easiest to just put here for now
    paintBorders(canvas, fragment);

    canvas.alterPaint(paint -> paint.incOffset(
      fragment.contentX() - fragment.borderX(),
      fragment.contentY() - fragment.borderY()));
  }

  private static void paintBorders(PaintCanvas canvas, BoxFragment fragment) {
    // Quick and dirty implementation, ignore styles for now
    float[] borders = fragment.box().dimensions().getComputedBorder();
    
    canvas.alterPaint(paint -> paint.setColor(fragment.box().activeStyles().borderTopColor()));
    canvas.drawBox(0, 0, fragment.borderWidth(), borders[0]);

    canvas.alterPaint(paint -> paint.setColor(fragment.box().activeStyles().borderBottomColor()));
    canvas.drawBox(0, fragment.borderHeight() - borders[1], fragment.borderWidth(), borders[1]);

    canvas.alterPaint(paint -> paint.setColor(fragment.box().activeStyles().borderLeftColor()));
    canvas.drawBox(0, 0, borders[2], fragment.borderHeight());

    canvas.alterPaint(paint -> paint.setColor(fragment.box().activeStyles().borderLeftColor()));
    canvas.drawBox(fragment.borderWidth() - borders[3], 0, borders[3], fragment.borderHeight());
  }

}
