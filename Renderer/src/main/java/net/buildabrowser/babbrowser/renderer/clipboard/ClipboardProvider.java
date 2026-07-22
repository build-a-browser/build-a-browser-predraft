package net.buildabrowser.babbrowser.renderer.clipboard;

import java.io.InputStream;
import java.net.URI;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.dom.Node;

public interface ClipboardProvider<T> {
  
  T createHtmlClip(Node node, String textFallback);

  T createImageClip(
    URI imageURI,
    Supplier<InputStream> imageBytesSupplier,
    String altText
  );

  void setActiveClip(T clip);

}
