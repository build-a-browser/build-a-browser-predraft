package net.buildabrowser.babbrowser.renderer.paint.painters.input;

import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.input.RadioBoxInputFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBackgroundPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementOutlinePainter;

public class RadioBoxInputBoxPainter implements BoxPainter<RadioBoxInputFragment> {

  @Override
  public void paint(
    RadioBoxInputFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    float width = fragment.width(Measurement.CONTENT);
    float height = fragment.height(Measurement.CONTENT);
    float size = Math.min(width, height);
    float posX = (width - size) / 2;
    float posY = (height - size) / 2;

    float innerSize = size * InputPaintConstants.SMALL_INPUT_INNER_SIZE_FACTOR;
    float innerPosOffset = size * (1 - InputPaintConstants.SMALL_INPUT_INNER_SIZE_FACTOR) / 2;

    HTMLInputElement inputElement = (HTMLInputElement) fragment.box().element();
    int backgroundColor = inputElement.disabled() ?
      InputPaintConstants.SMALL_INPUT_DISABLED_BACKGROUND_COLOR :
      InputPaintConstants.SMALL_INPUT_BACKGROUND_COLOR;
    int borderColor = inputElement.disabled() ?
      InputPaintConstants.SMALL_INPUT_DISABLED_BORDER_COLOR :
      InputPaintConstants.SMALL_INPUT_BORDER_COLOR;
    int fillColor = inputElement.disabled() ?
      InputPaintConstants.SMALL_INPUT_DISABLED_FILL_COLOR :
      InputPaintConstants.SMALL_INPUT_FILL_COLOR;

    canvas.withPaint(
      p -> p.setColor(backgroundColor),
      p -> p.drawCircle(posX, posY, size / 2));

    canvas.withPaint(
      p -> {
        p.setFilled(false);
        p.setColor(borderColor);
      },
      p -> p.drawCircle(posX, posY, size / 2));

    if (inputElement.checked()) {
      canvas.withPaint(
        p -> p.setColor(fillColor),
        p -> p.drawCircle(
          posX + innerPosOffset,
          posY + innerPosOffset,
          innerSize / 2));
    }
  }

  @Override
  public void paintBackground(
    RadioBoxInputFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    float fragmentWidth = fragment.width(Measurement.BORDER);
    float fragmentHeight = fragment.height(Measurement.BORDER);
    ElementOutlinePainter.paintOutlines(
      canvas, fragment, fragmentWidth, fragmentHeight);
    ElementBackgroundPainter.paintDebugOutlines(
      canvas, fragmentWidth, fragmentHeight);
  }
  
}
