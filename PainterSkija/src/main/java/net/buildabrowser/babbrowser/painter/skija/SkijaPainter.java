package net.buildabrowser.babbrowser.painter.skija;

import io.github.humbleui.skija.ImageInfo;
import io.github.humbleui.skija.Surface;
import net.buildabrowser.babbrowser.painter.core.PaintBitMap;
import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.painter.core.ResourceLoader;

public abstract class SkijaPainter implements Painter {

  private final ResourceLoader resourceLoader = new SkijaResourceLoader();

  private final boolean bitmapIsABitmap;

  public SkijaPainter(boolean bitmapIsABitmap) {
    this.bitmapIsABitmap = bitmapIsABitmap;
  }

  @Override
  public ResourceLoader resourceLoader() {
    return this.resourceLoader;
  }

  @Override
  public PaintBitMap createPaintBitMap(int width, int height) {
    if (bitmapIsABitmap) {
      Surface surface = Surface.makeRaster(
        ImageInfo.makeN32Premul(width, height));
      return new SkijaBitMap(surface);
    } else {
      return new SkijaCommandList(width, height);
    }
  }
  
}
