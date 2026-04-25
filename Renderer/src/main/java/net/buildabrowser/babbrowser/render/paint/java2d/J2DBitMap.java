package net.buildabrowser.babbrowser.render.paint.java2d;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.render.paint.PaintBitMap;
import net.buildabrowser.babbrowser.render.paint.PaintCanvas;

public class J2DBitMap implements PaintBitMap {

  private final BufferedImage bitMapImage;

  public J2DBitMap(BufferedImage bitMapImage) {
    this.bitMapImage = bitMapImage;
  }

  @Override
  public void withCanvas(Consumer<PaintCanvas> paintFunc) {
    J2DPaintCanvas canvas = new J2DPaintCanvas(bitMapImage.createGraphics());
    paintFunc.accept(canvas);
  }

  public BufferedImage image() {
    return this.bitMapImage;
  }

}
