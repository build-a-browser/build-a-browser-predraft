package net.buildabrowser.babbrowser.render.paint.java2d;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.render.paint.PaintBitMap;
import net.buildabrowser.babbrowser.render.paint.PaintCanvas;
import net.buildabrowser.babbrowser.render.paint.Painter;
import net.buildabrowser.babbrowser.render.paint.ResourceLoader;

public class Java2DPainter implements Painter {
  
  private final ResourceLoader resourceLoader = new J2DResourceLoader();

  @Override
  public ResourceLoader resourceLoader() {
    return this.resourceLoader;
  }

  @Override
  public PaintBitMap createPaintBitMap(int width, int height) {
    BufferedImage bitMapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    return new J2DBitMap(bitMapImage);
  }

  @Override
  public void withCanvas(Graphics g, int width, int height, Consumer<PaintCanvas> paintFunc) {
    paintFunc.accept(new J2DPaintCanvas((Graphics2D) g));
  }

}
