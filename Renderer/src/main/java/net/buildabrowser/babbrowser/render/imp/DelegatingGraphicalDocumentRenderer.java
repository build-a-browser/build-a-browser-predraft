package net.buildabrowser.babbrowser.render.imp;

import java.util.Optional;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.html.navigation.Navigable;
import net.buildabrowser.babbrowser.render.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.render.event.EventForwardingTarget;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.render.paint.backend.PaintCanvas;

public class DelegatingGraphicalDocumentRenderer implements GraphicalDocumentRenderer, EventForwardingTarget {

  private static final GraphicalDocumentRenderer NO_OP_RENDERER = new NoOpGraphicalDocumentRenderer();

  private final Navigable navigable;

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
  public void forwardEvent(RendererMouseEvent mouseEvent) {
    GraphicalDocumentRenderer activeRenderer = activeRenderer();
    if (activeRenderer instanceof EventForwardingTarget target) {
      target.forwardEvent(mouseEvent);
    }
  }

  @Override
  public void addRepaintListener(Runnable repaintListener) {
    navigable.uaNavigableOptions().addRepaintListener(repaintListener);
  }

  private GraphicalDocumentRenderer activeRenderer() {
    GraphicalDocumentRenderer activeRenderer = (GraphicalDocumentRenderer) navigable.activeDocument().renderer();
    return activeRenderer == null ?
      NO_OP_RENDERER :
      activeRenderer;
  }
  
}
