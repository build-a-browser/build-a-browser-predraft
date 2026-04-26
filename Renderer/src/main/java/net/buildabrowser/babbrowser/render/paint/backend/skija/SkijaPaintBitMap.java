package net.buildabrowser.babbrowser.render.paint.backend.skija;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import net.buildabrowser.babbrowser.render.paint.backend.PaintBitMap;

public interface SkijaPaintBitMap extends PaintBitMap {

  void draw(Canvas canvas, Paint paint, int x, int y);

}
