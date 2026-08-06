package net.buildabrowser.babbrowser.renderer.clipboard;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import net.buildabrowser.babbrowser.dom.Node;

public interface ClipboardProvider<T> {
  
  T createHtmlClip(Node node, String textFallback);

  T createImageClip(
    URI imageURI,
    IOThrowingSupplier<InputStream> imageBytesSupplier,
    String altText
  );

  void setActiveClip(T clip);

  interface IOThrowingSupplier<T> {
    T get() throws IOException;
  }

}
