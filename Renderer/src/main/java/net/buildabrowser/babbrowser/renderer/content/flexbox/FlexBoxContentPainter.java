package net.buildabrowser.babbrowser.renderer.content.flexbox;

import net.buildabrowser.babbrowser.renderer.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.content.common.paint.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.renderer.layout.StackingContext;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.PaintUtil;
import net.buildabrowser.babbrowser.renderer.paint.backend.PaintCanvas;

public class FlexBoxContentPainter implements BoxPainter {

  private final FlexBoxContent content;

  public FlexBoxContentPainter(FlexBoxContent content) {
    this.content = content;
  }

  @Override
  public void paint(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
    StackingContext refContext = fragment.box().stackingContext();
    BoxFragment nextChild = content.fragments();
    while (nextChild != null) {
      PaintUtil.maybePaintFragment(nextChild, canvas, vpIntersection,
        (f, c, vpi) -> paintChild(f, c, vpi, refContext));
      nextChild = (BoxFragment) nextChild.next();
    }
  }

  @Override
  public void paintBackground(BoxFragment fragment, PaintCanvas canvas, int[] vpIntersection) {
    ElementBackgroundPainter.paintBackground(canvas, fragment);
  }

  private void paintChild(BoxFragment child, PaintCanvas canvas, int[] vpIntersection, StackingContext refContext) {
    if (child.box().stackingContext() != refContext) return;
      
    canvas.pushPaint();
    canvas.alterPaint(p -> p.incOffset(child.posX(Measurement.BORDER), child.posY(Measurement.BORDER)));
    child.painter().paintBackground(child, canvas, vpIntersection);
    canvas.popPaint();

    canvas.pushPaint();
    canvas.alterPaint(p -> p.incOffset(child.posX(Measurement.CONTENT), child.posY(Measurement.CONTENT)));
    child.painter().paint(child, canvas, vpIntersection);
    canvas.popPaint();
  }

}
