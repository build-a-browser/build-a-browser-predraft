package net.buildabrowser.babbrowser.render.imp;

import java.util.Optional;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.render.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public class NoOpGraphicalDocumentRenderer implements GraphicalDocumentRenderer {

  @Override
  public boolean shouldRender() {
    return false;
  }

  @Override
  public void recalculateStyles() {}

  @Override
  public void updateLayout() {}

  @Override
  public void updateRendering() {}

  @Override
  public void resize(int width, int height) {}

  @Override
  public void draw(PaintCanvas context) {}

  @Override
  public Optional<String> title() {
    return Optional.empty();
  }

  @Override
  public DocumentChangeListener changeListener() {
    return new DocumentChangeListener() {};
  }

  @Override
  public void addRepaintListener(Runnable repaintListener) {

  }

  @Override
  public void onDocumentInvalidated(InvalidationLevel invalidationLevel) {}
  
}
