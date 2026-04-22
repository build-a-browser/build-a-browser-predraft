package net.buildabrowser.babbrowser.html.navigation;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public interface DocumentRenderer {
  
  boolean shouldRender();

  void recalculateStyles();

  void updateLayout();

  void updateRendering();

  void resize(int width, int height);

  void withImage(Consumer<BufferedImage> func);

  DocumentChangeListener changeListener();
  
  void onDocumentInvalidated(InvalidationLevel invalidationLevel);

  void setEventListener(DocumentRendererEventListener eventListener);

  DocumentRendererEventListener eventListener();

  interface DocumentRendererEventListener {

    void onNavigate(URI url);

    void onTitleChanged(String title);

  }

}
