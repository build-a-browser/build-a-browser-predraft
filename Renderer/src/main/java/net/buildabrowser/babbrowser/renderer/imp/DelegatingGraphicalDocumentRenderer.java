package net.buildabrowser.babbrowser.renderer.imp;

import java.io.IOException;
import java.util.Optional;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.renderer.event.EventForwardingTarget;

public class DelegatingGraphicalDocumentRenderer implements GraphicalDocumentRenderer {

  private static final GraphicalDocumentRenderer NO_OP_RENDERER = new NoOpGraphicalDocumentRenderer();

  private final Navigable navigable;

  private int width, height;

  public DelegatingGraphicalDocumentRenderer(Navigable navigable) {
    this.navigable = navigable;
  }

  @Override
  public boolean shouldRender() {
    return activeRenderer().shouldRender();
  }

  @Override
  public void recalculateStyles() {
    activeRenderer().recalculateStyles();
  }

  @Override
  public void updateLayout() {
    activeRenderer().updateLayout();
  }

  @Override
  public void updateRendering() {
    activeRenderer().updateRendering();
  }

  @Override
  public void resize(int width, int height) {
    this.width = width;
    this.height = height;
    activeRenderer().resize(width, height);
  }

  @Override
  public void draw(PaintCanvas context) {
    activeRenderer().draw(context);
  }

  @Override
  public DocumentChangeListener changeListener() {
    return activeRenderer().changeListener();
  }

  @Override
  public void onDocumentInvalidated(InvalidationLevel invalidationLevel) {
    activeRenderer().onDocumentInvalidated(invalidationLevel);
  }

  @Override
  public Optional<String> title() {
    return activeRenderer().title();
  }

  @Override
  public EventForwardingTarget eventForwardingTarget() {
    return activeRenderer().eventForwardingTarget();
  }

  @Override
  public void close() throws IOException {
    activeRenderer().close();
  }

  public void onInnerRendererChanged() {
    resize(width, height);
  }

  private GraphicalDocumentRenderer activeRenderer() {
    GraphicalDocumentRenderer activeRenderer = (GraphicalDocumentRenderer) navigable.activeDocument().renderer();
    return activeRenderer == null ?
      NO_OP_RENDERER :
      activeRenderer;
  }
  
}
