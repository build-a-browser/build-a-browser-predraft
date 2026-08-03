package net.buildabrowser.babbrowser.browser.clipboard;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

public class HTMLTransferable implements Transferable {

  private static final List<DataFlavor> SUPPORTED_FLAVORS = List.of(
    DataFlavor.allHtmlFlavor, DataFlavor.stringFlavor);

  private final String htmlContent;
  private final String textContent;

  public HTMLTransferable(String htmlContent, String textContent) {
    this.htmlContent = htmlContent;
    this.textContent = textContent;
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
    if (flavor.equals(DataFlavor.allHtmlFlavor)) return htmlContent;
    if (flavor.equals(DataFlavor.stringFlavor)) return textContent;
    throw new UnsupportedOperationException("Unsuppported flavor: " + flavor);
  }
  
}
