package net.buildabrowser.babbrowser.painter.backend.skija;

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

import javax.swing.JPanel;

import io.github.humbleui.skija.Image;
import io.github.humbleui.skija.ImageInfo;
import io.github.humbleui.skija.Pixmap;
import io.github.humbleui.skija.Surface;
import net.buildabrowser.babbrowser.renderer.paint.backend.CanvasCallbacks;

public class SkijaSoftwareCanvas extends JPanel {
  
  private final CanvasCallbacks callbacks;

  public SkijaSoftwareCanvas(CanvasCallbacks callbacks) {
    this.callbacks = callbacks;
  }

  @Override
  public void doLayout() {
    super.doLayout();
    callbacks.layout(getWidth(), getHeight());
  }

  @Override
  public void paint(Graphics g) {
    Surface surface = Surface.makeRaster(
      ImageInfo.makeN32Premul(getWidth(), getHeight()));
    
    callbacks.paint(new SkijaPaintCanvas(surface.getCanvas()));

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
