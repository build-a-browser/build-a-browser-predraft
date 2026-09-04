package net.buildabrowser.babbrowser.renderer.paint.painters.input;

import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.InputContent;
import net.buildabrowser.babbrowser.renderer.content.input.text.TextTypeContent;
import net.buildabrowser.babbrowser.renderer.fragment.input.TextInputFragment;
import net.buildabrowser.babbrowser.renderer.input.TextController;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.TextEditPainter;

public class TextInputBoxPainter implements BoxPainter<TextInputFragment> {

  @Override
  public void paint(
    TextInputFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    ElementBox box = fragment.box();
    TextTypeContent content = ((InputContent) box.content()).innerContent(box);
    TextController controller = content.textController();
    TextEditPainter.paint(canvas, controller, fragment);
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
