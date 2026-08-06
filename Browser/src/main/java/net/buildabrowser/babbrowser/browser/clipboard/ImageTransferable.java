package net.buildabrowser.babbrowser.browser.clipboard;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import net.buildabrowser.babbrowser.renderer.clipboard.ClipboardProvider.IOThrowingSupplier;

public class ImageTransferable implements Transferable {

  private static final List<DataFlavor> SUPPORTED_FLAVORS = List.of(
    DataFlavor.imageFlavor);
    
  private final BufferedImage image;

  public ImageTransferable(IOThrowingSupplier<InputStream> imageBytesSupplier) throws IOException {
    this.image = ImageIO.read(imageBytesSupplier.get());
    if (image == null) {
      throw new IOException("Failed to load image");
    }
  }

  @Override
  public DataFlavor[] getTransferDataFlavors() {
    return SUPPORTED_FLAVORS.toArray(DataFlavor[]::new);
  }

  @Override
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return SUPPORTED_FLAVORS.contains(flavor);
  }

  @Override
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    return this.image;
  }

}
