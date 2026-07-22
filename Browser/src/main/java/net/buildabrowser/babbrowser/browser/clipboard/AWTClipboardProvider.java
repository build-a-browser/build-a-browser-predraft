package net.buildabrowser.babbrowser.browser.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.util.HTMLSerializerUtil;
import net.buildabrowser.babbrowser.renderer.clipboard.ClipboardProvider;

public class AWTClipboardProvider implements ClipboardProvider<Transferable>, ClipboardOwner {

  private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

  @Override
  public Transferable createHtmlClip(Node node, String textFallback) {
    return new HTMLTransferable(
      HTMLSerializerUtil.serializeNode(node),
      textFallback);
  }

  @Override
  public Transferable createImageClip(URI imageURI, Supplier<InputStream> imageBytesSupplier, String altText) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'createImageClip'");
  }

  @Override
  public void setActiveClip(Transferable clip) {
    clipboard.setContents(clip, this);
  }
  
  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents) {}
  
}
