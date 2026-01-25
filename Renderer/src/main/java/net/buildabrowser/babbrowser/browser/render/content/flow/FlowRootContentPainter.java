package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.floatbox.FloatTracker;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.TextFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;

public final class FlowRootContentPainter {
 
  private FlowRootContentPainter() {}

  public static void paint(PaintCanvas canvas, FlowRootContent rootContent, ElementBox rootBox) {
    ManagedBoxFragment rootFragment = rootContent.rootFragment();

    canvas.pushPaint();
    if (rootBox.element().name().equals("html")) {
      paintBackgroundAndAdvance(canvas, rootFragment);
    }

    paintBlockLevelBackgrounds(canvas, rootFragment);
    paintFloats(canvas, rootContent.floatTracker());
    paintFragment(canvas, rootFragment);
    canvas.popPaint();
  }

  private static void paintBlockLevelBackgrounds(PaintCanvas canvas, ManagedBoxFragment fragment) {
    for (FlowFragment childFragment: fragment.fragments()) {
      canvas.pushPaint();
      canvas.alterPaint(paint -> paint.incOffset(
        childFragment.borderX() + childFragment.paintOffsetX(),
        childFragment.borderY() + childFragment.paintOffsetY()));
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
    for (FlowFragment childFragment: floatTracker.allFloats()) {
      canvas.pushPaint();
      canvas.alterPaint(paint -> paint.incOffset(
        childFragment.borderX() + childFragment.paintOffsetX(),
        childFragment.borderY() + childFragment.paintOffsetY()));
      paintBackgroundAndAdvance(canvas, (FlowBoxFragment) childFragment);
      paintFragment(canvas, childFragment);
      canvas.popPaint();
    }
  }

  public static void paintFragment(PaintCanvas canvas, FlowFragment fragment) {
    switch (fragment) {
      case ManagedBoxFragment boxFragment -> paintManagedBoxFragment(canvas, boxFragment);
      case UnmanagedBoxFragment boxFragment -> boxFragment.box().content().paint(canvas);
      case LineBoxFragment lineboxFragment -> paintLineBoxFragment(canvas, lineboxFragment);
      default -> throw new UnsupportedOperationException("Unrecognized Fragment Type!");
    }
  }

  private static void paintManagedBoxFragment(PaintCanvas canvas, ManagedBoxFragment fragment) {
    ElementBox parentBox = fragment.box();
    for (FlowFragment childFragment: fragment.fragments()) {
      canvas.pushPaint();
      canvas.alterPaint(paint -> paint.incOffset(
        childFragment.contentX() + childFragment.paintOffsetX(),
        childFragment.contentY() + childFragment.paintOffsetY()));
      canvas.alterPaint(paint -> paint.setColor(parentBox.activeStyles().textColor()));
      paintFragment(canvas, childFragment);
      canvas.popPaint();
    }
  }

  // TODO: Unify this with above? Inline is offseting by border (then child adjusts), block-level by content
  private static void paintInlineFragment(PaintCanvas canvas, FlowFragment fragment) {
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
    for (FlowFragment childFragment: fragment.fragments()) {
      canvas.pushPaint();
      canvas.alterPaint(paint -> paint.incOffset(
        childFragment.contentX() + childFragment.paintOffsetX(),
        childFragment.contentY() + childFragment.paintOffsetY()));
      canvas.alterPaint(paint -> paint.setColor(parentBox.activeStyles().textColor()));
      paintInlineFragment(canvas, childFragment);
      canvas.popPaint();
    }
  }

  private static void paintInlineUnmanagedBoxFragment(PaintCanvas canvas, UnmanagedBoxFragment fragment) {
    paintBackgroundAndAdvance(canvas, fragment);
    fragment.box().content().paint(canvas);
  }

  private static void paintTextFragment(PaintCanvas canvas, TextFragment textFragment) {
    canvas.drawText(0, 0, textFragment.text());
  }

  private static void paintLineBoxFragment(PaintCanvas canvas, LineBoxFragment lineboxFragment) {
    for (FlowFragment fragment: lineboxFragment.fragments()) {
      canvas.pushPaint();
      canvas.alterPaint(paint -> paint.incOffset(
        fragment.borderX() + fragment.paintOffsetX(),
        fragment.borderY() + fragment.paintOffsetY()));
      paintInlineFragment(canvas, fragment);
      canvas.popPaint();
    }
  }

  private static void paintBackgroundAndAdvance(PaintCanvas canvas, FlowBoxFragment fragment) {
    canvas.alterPaint(paint -> paint.setColor(fragment.box().activeStyles().backgroundColor()));
    canvas.drawBox(0, 0, fragment.borderWidth(), fragment.borderHeight());

    // Might need changed when adding tables, but usually background is followed by border,
    // so it is easiest to just put here for now
    paintBorders(canvas, fragment);

    canvas.alterPaint(paint -> paint.incOffset(
      fragment.contentX() - fragment.borderX(),
      fragment.contentY() - fragment.borderY()));
  }

  private static void paintBorders(PaintCanvas canvas, FlowBoxFragment fragment) {
    // Quick and dirty implementation, ignore styles for now
    int[] borders = fragment.box().dimensions().getComputedBorder();
    
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
