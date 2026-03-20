package net.buildabrowser.babbrowser.browser.render.content.flexbox;

import net.buildabrowser.babbrowser.browser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.browser.render.content.common.paint.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.browser.render.layout.StackingContext;
import net.buildabrowser.babbrowser.browser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;

public class FlexBoxContentPainter implements BoxPainter {

  private final FlexBoxContent content;

  public FlexBoxContentPainter(FlexBoxContent content) {
    this.content = content;
  }

  @Override
  public void paint(BoxFragment fragment, PaintCanvas canvas) {
    StackingContext refContext = fragment.box().stackingContext();
    BoxFragment nextChild = content.fragments();
    while (nextChild != null) {
      BoxFragment child = nextChild;
      nextChild = (BoxFragment) nextChild.next();

      if (child.box().stackingContext() != refContext) continue;
      
      canvas.pushPaint();
      canvas.alterPaint(p -> p.incOffset(child.borderX(), child.borderY()));
      child.painter().paintBackground(child, canvas);
      canvas.popPaint();

      canvas.pushPaint();
      canvas.alterPaint(p -> p.incOffset(child.contentX(), child.contentY()));
      child.painter().paint(child, canvas);
      canvas.popPaint();
    }
  }

  @Override
  public void paintBackground(BoxFragment fragment, PaintCanvas canvas) {
    ElementBackgroundPainter.paintBackground(canvas, fragment);
  }

}
