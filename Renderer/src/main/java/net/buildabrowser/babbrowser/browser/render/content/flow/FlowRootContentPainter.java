package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.FlowFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.LineBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.ManagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.TextFragment;
import net.buildabrowser.babbrowser.browser.render.content.flow.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;

public final class FlowRootContentPainter {
 
  private FlowRootContentPainter() {}

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
    canvas.alterPaint(paint -> paint.setColor(parentBox.activeStyles().backgroundColor()));
    canvas.drawBox(fragment.posX(), fragment.posY(), fragment.width(), fragment.height());
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
