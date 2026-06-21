package net.buildabrowser.babbrowser.renderer.paint.painters.input;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.fragment.input.TextInputFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBackgroundPainter;

public class TextInputBoxPainter implements BoxPainter<TextInputFragment> {

  @Override
  public void paint(
    TextInputFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    
  }

  @Override
  public void paintBackground(
    TextInputFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    ElementBackgroundPainter.paintBackground(
      canvas, fragment, vpIntersection);
  }
  
}
