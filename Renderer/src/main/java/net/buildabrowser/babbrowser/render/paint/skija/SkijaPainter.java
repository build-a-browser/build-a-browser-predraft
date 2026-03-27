package net.buildabrowser.babbrowser.render.paint.skija;

import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Image;
import io.github.humbleui.skija.ImageInfo;
import io.github.humbleui.skija.Pixmap;
import io.github.humbleui.skija.Surface;
import net.buildabrowser.babbrowser.render.paint.PaintCanvas;
import net.buildabrowser.babbrowser.render.paint.Painter;
import net.buildabrowser.babbrowser.render.paint.ResourceLoader;

public class SkijaPainter implements Painter {

  private final ResourceLoader resourceLoader = new SkijaResourceLoader();

  @Override
  public ResourceLoader resourceLoader() {
    return this.resourceLoader;
  }

  @Override
  public void withCanvas(Graphics g, int width, int height, Consumer<PaintCanvas> paintFunc) {
    Surface surface = Surface.makeRaster(
      ImageInfo.makeN32Premul(width, height));
    Canvas rawCanvas = surface.getCanvas();
    PaintCanvas canvas = new SkijaPaintCanvas(rawCanvas);
    paintFunc.accept(canvas);
    Image image = surface.makeImageSnapshot();
    BufferedImage bufferedImage = toBufferedImage(image, surface.getImageInfo());
    g.drawImage(bufferedImage, 0, 0, null);
  }

  private static BufferedImage toBufferedImage(Image rawImage, ImageInfo imageInfo) {
    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(imageInfo.getWidth() * imageInfo.getHeight() * 4);
    Pixmap pixmap = Pixmap.make(imageInfo, byteBuffer, imageInfo.getWidth() * 4);
    if (!rawImage.readPixels(pixmap, 0, 0, false)) {
      throw new RuntimeException("Failed to read pixel buffer!");
    }
    byte[] bytes = new byte[byteBuffer.remaining()];
    byteBuffer.get(bytes);

    DataBufferByte buffer = new DataBufferByte(bytes, bytes.length);

    WritableRaster raster = Raster.createInterleavedRaster(
      buffer, imageInfo.getWidth(), imageInfo.getHeight(),
      imageInfo.getWidth() * 4, 4, new int[] { 2, 1, 0, 3 }, null);
    ColorModel colorModel = new ComponentColorModel(
      ColorSpace.getInstance(ColorSpace.CS_sRGB), true, true,
      Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
    BufferedImage bufferedImage = new BufferedImage(colorModel, raster, false, null);

    return bufferedImage;
  }
  
}
