package net.buildabrowser.babbrowser.renderer.paint.painters.input;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.fragment.input.HiddenInputFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;

public class HiddenInputBoxPainter implements BoxPainter<HiddenInputFragment> {

  @Override
  public void paint(
    HiddenInputFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection
  ) {}

  @Override
  public void paintBackground(
    HiddenInputFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection
  ) {}
  
}
