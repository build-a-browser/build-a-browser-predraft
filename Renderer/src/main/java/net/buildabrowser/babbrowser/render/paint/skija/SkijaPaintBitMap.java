package net.buildabrowser.babbrowser.render.paint.skija;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import net.buildabrowser.babbrowser.render.paint.PaintBitMap;

public interface SkijaPaintBitMap extends PaintBitMap {

  void draw(Canvas canvas, Paint paint, int x, int y);

}
