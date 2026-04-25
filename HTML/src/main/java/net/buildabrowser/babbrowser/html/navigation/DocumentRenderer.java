package net.buildabrowser.babbrowser.html.navigation;

import java.awt.Graphics;
import java.net.URI;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;

public interface DocumentRenderer {
  
  boolean shouldRender();

  void recalculateStyles();

  void updateLayout();

  void updateRendering();

  void resize(int width, int height);

  void draw(Graphics g);

  DocumentChangeListener changeListener();
  
  void onDocumentInvalidated(InvalidationLevel invalidationLevel);

  void setEventListener(DocumentRendererEventListener eventListener);

  DocumentRendererEventListener eventListener();

  interface DocumentRendererEventListener {

    void onNavigate(URI url);

    void onTitleChanged(String title);

  }

}
