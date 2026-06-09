package net.buildabrowser.babbrowser.renderer.paint.painters.flexbox;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.flexbox.FlexBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.StackingContext;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.PaintUtil;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBackgroundPainter;

public class FlexBoxPainter implements BoxPainter<FlexBoxFragment> {

  @Override
  public void paint(FlexBoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
    StackingContext refContext = fragment.box().stackingContext();
    BoxFragment<?> nextChild = fragment.fragments();
    while (nextChild != null) {
      PaintUtil.maybePaintFragment(nextChild, canvas, vpIntersection,
        (f, c, vpi) -> paintChild(f, c, vpi, refContext));
      nextChild = (BoxFragment<?>) nextChild.next();
    }
  }

  @Override
  public void paintBackground(FlexBoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
    ElementBackgroundPainter.paintBackground(canvas, fragment, vpIntersection);
  }

  private void paintChild(BoxFragment<?> child, PaintCanvas canvas, VpIntersection vpIntersection, StackingContext refContext) {
    if (child.box().stackingContext() != refContext) return;
    
    canvas.withTransform(
      t -> t.translate(child.posX(Measurement.BORDER), child.posY(Measurement.BORDER)),
      c -> child.withPainterV((p, f) -> p.paintBackground(f, c, vpIntersection)));

    canvas.withTransform(
      t -> t.translate(child.posX(Measurement.CONTENT), child.posY(Measurement.CONTENT)),
      c -> child.withPainterV((p, f) -> p.paint(f, c, vpIntersection)));
  }

}
