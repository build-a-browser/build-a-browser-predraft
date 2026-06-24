package net.buildabrowser.babbrowser.renderer.paint.painters.input;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.fragment.input.ButtonInputFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.PaintUtil;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBackgroundPainter;

public class ButtonInputBoxPainter implements BoxPainter<ButtonInputFragment> {

  @Override
  public void paint(ButtonInputFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection) {
    PaintUtil.maybePaintFragment(
      fragment.innerFragment(), canvas, vpIntersection,
      (f, c, vpi) -> f.withPainterV((p, f2) -> p.paint(f2, c, vpi)));
  }

  @Override
  public void paintBackground(
    ButtonInputFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    ElementBackgroundPainter.paintBackground(canvas, fragment, vpIntersection);
    // Skip inner fragment root for now, might need changed later
  }
  
}
