package net.buildabrowser.babbrowser.renderer.paint.painters.image;

import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.painter.core.LoadedImage;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.context.SelectionContext;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.image.ImageBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBackgroundPainter;

public class ImageBoxPainter implements BoxPainter<ImageBoxFragment> {

  @Override
  public void paint(
    ImageBoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection
  ) {
    LoadedImage image = fragment.image();
    ElementBox box = fragment.box();

    float width = fragment.width(Measurement.CONTENT);
    float height = fragment.height(Measurement.CONTENT);

    canvas.withPaint(
      paint -> paint.setColor(PropertiesUtil.backgroundColor(box.properties())),
      c -> c.drawBox(0, 0, width, height));

    if (image != null) {
      canvas.drawImage(0, 0, width, height, image);

      SelectionContext selectionContext = box.layoutContext().global().selectionContext();
      int selectionBgColor = PropertiesUtil.selectionBgColor(box.properties());
      if (selectionContext.selected(box.element())) {
        canvas.withPaint(
        p -> p.setColor(selectionBgColor),
        c -> c.drawBox(0, 0, width, height));
      }
      return;
    }

    canvas.withPaint(
      p -> {
        p.setFont(box.layoutContext().font());
        p.setColor(PropertiesUtil.textColor(box.properties()));
      },
      c -> c.drawText(0, 0, fragment.altText()));
  }

  @Override
  public void paintBackground(
    ImageBoxFragment fragment, PaintCanvas canvas, VpIntersection vpIntersection
  ) {
    ElementBackgroundPainter.paintBackground(canvas, fragment, vpIntersection);
  }
  
}
