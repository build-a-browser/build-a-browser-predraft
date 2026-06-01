package net.buildabrowser.babbrowser.painter.skija;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Paint;
import net.buildabrowser.babbrowser.painter.core.PaintBitMap;

public interface SkijaPaintBitMap extends PaintBitMap {

  void draw(Canvas canvas, Paint paint, int x, int y);

}
