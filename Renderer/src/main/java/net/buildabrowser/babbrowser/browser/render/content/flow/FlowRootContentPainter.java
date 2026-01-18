package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.floatbox.FloatTracker;
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
    canvas.alterPaint(paint -> paint.setColor(rootBox.activeStyles().backgroundColor()));
    canvas.drawBox(0, 0, rootFragment.width(), rootFragment.height());

    paintBlockLevelBackgrounds(canvas, rootFragment);
    paintFloats(canvas, rootContent.floatTracker());
    paintFragment(canvas, rootFragment);
  }

  private static void paintBlockLevelBackgrounds(PaintCanvas canvas, ManagedBoxFragment fragment) {
    for (FlowFragment childFragment: fragment.fragments()) {
      canvas.pushPaint();
      canvas.alterPaint(paint -> paint.incOffset(childFragment.posX(), childFragment.posY()));
      switch (childFragment) {
        case ManagedBoxFragment managedFragment:
          canvas.alterPaint(paint -> paint.setColor(managedFragment.box().activeStyles().backgroundColor()));
          canvas.drawBox(0, 0, managedFragment.width(), managedFragment.height());
          paintBlockLevelBackgrounds(canvas, managedFragment);
          break;
        case UnmanagedBoxFragment unmanagedFragment:
          canvas.alterPaint(paint -> paint.setColor(unmanagedFragment.box().activeStyles().backgroundColor()));
          canvas.drawBox(0, 0, unmanagedFragment.width(), unmanagedFragment.height());
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
      canvas.alterPaint(paint -> paint.incOffset(childFragment.posX(), childFragment.posY()));
      paintFragment(canvas, childFragment);
      canvas.popPaint();
    }
  }

  public static void paintFragment(PaintCanvas canvas, FlowFragment fragment) {
    switch (fragment) {
      case ManagedBoxFragment boxFragment -> paintManagedBoxFragement(canvas, boxFragment);
      case UnmanagedBoxFragment boxFragment -> boxFragment.box().content().paint(canvas);
      case TextFragment textFragment -> paintTextFragment(canvas, textFragment);
      case LineBoxFragment lineboxFragment -> paintLineBoxFragment(canvas, lineboxFragment);
      default -> throw new UnsupportedOperationException("Unrecognized Fragment Type!");
    }
  }

  private static void paintManagedBoxFragement(PaintCanvas canvas, ManagedBoxFragment fragment) {
    ElementBox parentBox = fragment.box();
    for (FlowFragment childFragment: fragment.fragments()) {
      canvas.pushPaint();
      canvas.alterPaint(paint -> paint.incOffset(childFragment.posX(), childFragment.posY()));
      canvas.alterPaint(paint -> paint.setColor(parentBox.activeStyles().textColor()));
      paintFragment(canvas, childFragment);
      canvas.popPaint();
    }
  }

  private static void paintTextFragment(PaintCanvas canvas, TextFragment textFragment) {
    canvas.drawText(0, 0, textFragment.text());
  }

  private static void paintLineBoxFragment(PaintCanvas canvas, LineBoxFragment lineboxFragment) {
    for (FlowFragment fragment: lineboxFragment.fragments()) {
      canvas.pushPaint();
      canvas.alterPaint(paint -> paint.incOffset(fragment.posX(), fragment.posY()));
      paintFragment(canvas, fragment);
      canvas.popPaint();
    }
  }

}
