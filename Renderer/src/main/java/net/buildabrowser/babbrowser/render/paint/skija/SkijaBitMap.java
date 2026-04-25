package net.buildabrowser.babbrowser.render.paint.skija;

import java.util.function.Consumer;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Surface;
import net.buildabrowser.babbrowser.render.paint.PaintBitMap;
import net.buildabrowser.babbrowser.render.paint.PaintCanvas;

public class SkijaBitMap implements PaintBitMap {

  private final Surface surface;

  public SkijaBitMap(Surface surface) {
    this.surface = surface;
  }

  @Override
  public void withCanvas(Consumer<PaintCanvas> paintFunc) {
    Canvas rawCanvas = surface.getCanvas();
    rawCanvas.save();
    rawCanvas.clear(0);
    PaintCanvas canvas = new SkijaPaintCanvas(rawCanvas);
    paintFunc.accept(canvas);
    rawCanvas.restore();
  }

  public void draw(Canvas canvas, Paint paint, int x, int y) {
    surface.draw(canvas, x, y, paint);
  }
  
}
