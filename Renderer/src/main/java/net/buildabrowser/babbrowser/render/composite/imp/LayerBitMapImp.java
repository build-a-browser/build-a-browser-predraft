package net.buildabrowser.babbrowser.render.composite.imp;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.render.composite.LayerBitMap;
import net.buildabrowser.babbrowser.render.paint.backend.PaintBitMap;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;
import net.buildabrowser.babbrowser.render.paint.backend.Painter;

public class LayerBitMapImp implements LayerBitMap {

  private final Object resizeLock = new Object();
  private final Object readyImageLock = new Object();

  private final Painter backingPainter;

  private short resizeCount;
  private int activeX, activeY;
  private int readyX, readyY;
  private int width, height;

  private PaintBitMap readyImage;
  private PaintBitMap activeImage;

  public LayerBitMapImp(Painter backingPainter) {
    this.backingPainter = backingPainter;
  }
  
  @Override
  public void update(Consumer<PaintCanvas> paintFunc) {
    synchronized (resizeLock) {
      if (this.resizeCount > 0) {
        this.resizeCount--;
        if (width <= 0 || height <= 0) {
          this.activeImage = null;
          synchronized (readyImageLock) {
            this.readyImage = null;
          }
          return;
        }
        this.activeImage = backingPainter.createPaintBitMap(width, height);
      }
    }
    
    if (this.activeImage != null) {
      activeImage.withCanvas(canvas -> {
        canvas.alterPaint(p -> p.incOffset(-this.activeX, -this.activeY));
        paintFunc.accept(canvas);
      });
    }

    synchronized (readyImageLock) {
      PaintBitMap prevImage = this.readyImage;
      this.readyImage = this.activeImage;
      this.activeImage = prevImage;
      this.readyX = this.activeX;
      this.readyY = this.activeY;
    }
  }

  @Override
  public void draw(int x, int y, PaintCanvas canvas) {
    synchronized (readyImageLock) {
      if (this.readyImage == null) return;
      canvas.drawBitMap(x + this.readyX, y + this.readyY, readyImage);
    }
  }

  @Override
  public void resize(int x, int y, int width, int height) {
    synchronized (resizeLock) {
      this.activeX = x;
      this.activeY = y;

      if (
        this.width == width
        && this.height == height
      ) return;

      this.width = width;
      this.height = height;
      this.resizeCount = 2;
    }
  }

}
