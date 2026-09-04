package net.buildabrowser.babbrowser.renderer.paint.painters.textarea;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.textarea.TextAreaContent;
import net.buildabrowser.babbrowser.renderer.fragment.textarea.TextAreaBoxFragment;
import net.buildabrowser.babbrowser.renderer.input.TextController;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.TextEditPainter;

public class TextAreaBoxPainter implements BoxPainter<TextAreaBoxFragment> {

  @Override
  public void paint(
    TextAreaBoxFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    ElementBox box = fragment.box();
    TextAreaContent content = (TextAreaContent) box.content();
    TextController controller = content.textController();
    TextEditPainter.paint(canvas, controller, fragment);
  }

  @Override
  public void paintBackground(
    TextAreaBoxFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    ElementBackgroundPainter.paintBackground(
      canvas, fragment, vpIntersection);
  }
  
}
