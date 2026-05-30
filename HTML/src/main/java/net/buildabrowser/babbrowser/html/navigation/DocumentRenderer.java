package net.buildabrowser.babbrowser.html.navigation;

import java.io.Closeable;
import java.net.URI;
import java.util.Optional;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public interface DocumentRenderer extends Closeable {
  
  boolean shouldRender();

  void recalculateStyles();

  void updateLayout();

  void updateRendering();

  Optional<String> title();

  DocumentChangeListener changeListener();
  
  void onDocumentInvalidated(InvalidationLevel invalidationLevel);

  default void close() {}

  interface DocumentRendererEventListener {

    void onNavigate(URI url);

    void onTitleChanged(String title);

  }

}
