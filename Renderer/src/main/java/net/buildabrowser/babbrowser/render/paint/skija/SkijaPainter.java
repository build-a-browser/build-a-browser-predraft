package net.buildabrowser.babbrowser.render.paint.skija;

import java.awt.Component;

import io.github.humbleui.skija.ImageInfo;
import io.github.humbleui.skija.Surface;
import net.buildabrowser.babbrowser.render.paint.CanvasCallbacks;
import net.buildabrowser.babbrowser.render.paint.PaintBitMap;
import net.buildabrowser.babbrowser.render.paint.Painter;
import net.buildabrowser.babbrowser.render.paint.ResourceLoader;

public class SkijaPainter implements Painter {

  private final ResourceLoader resourceLoader = new SkijaResourceLoader();

  private final boolean isSoftwareRendered;
  private final boolean bitmapIsABitmap;

  public SkijaPainter(boolean isSoftwareRendered, boolean bitmapIsABitmap) {
    this.isSoftwareRendered = isSoftwareRendered;
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

  @Override
  public Component createComponent(CanvasCallbacks callbacks) {
    if (isSoftwareRendered) {
      return new SkijaSoftwareCanvas(callbacks);
    } else {
      return new SkijaGPUCanvas(callbacks);
    }
  }
  
}
