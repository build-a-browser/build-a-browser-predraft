package net.buildabrowser.babbrowser.renderer.imp;

import java.util.Optional;

import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.renderer.api.FrameAPIs;
import net.buildabrowser.babbrowser.renderer.event.EventForwardingTarget;

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
  public EventForwardingTarget eventForwardingTarget() {
    return new EventForwardingTarget() {};
  }

  @Override
  public Optional<String> title() {
    return Optional.empty();
  }

  @Override
  public DocumentChangeListener changeListener() {
    return new DocumentChangeListener() {};
  }

  @Override
  public void onDocumentInvalidated(short invalidationLevel) {}

  @Override
  public FrameAPIs frameAPIs() {
    return null;
  }
  
}
