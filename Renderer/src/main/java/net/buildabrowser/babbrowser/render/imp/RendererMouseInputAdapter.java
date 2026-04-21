package net.buildabrowser.babbrowser.render.imp;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.function.Supplier;

import javax.swing.event.MouseInputAdapter;

import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.render.event.EventForwardingTarget;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent.MouseEventType;

public class RendererMouseInputAdapter extends MouseInputAdapter {

  private final Supplier<DocumentRenderer> rendererSupplier;

  public RendererMouseInputAdapter(Supplier<DocumentRenderer> rendererSupplier) {
    this.rendererSupplier = rendererSupplier;
  }
  
  @Override
  public void mouseClicked(MouseEvent e) {
    // TODO: Translate button
    e.consume();
    RendererMouseEvent mouseEvent = RendererMouseEvent.create(e.getX(), e.getY(), e.getButton(), MouseEventType.CLICK);
    if (rendererSupplier.get() instanceof EventForwardingTarget target) {
      target.forwardEvent(mouseEvent);
    }
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    // TODO: Translate button
    e.consume();
    RendererMouseEvent mouseEvent = RendererMouseEvent.create(e.getX(), e.getY(), e.getButton(), MouseEventType.MOVE);
    if (rendererSupplier.get() instanceof EventForwardingTarget target) {
      target.forwardEvent(mouseEvent);
    }
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    e.consume();
    // TODO: Some mice (not mine) have horizontal scroll wheels, how to detect that?
    RendererMouseEvent mouseEvent = e.isShiftDown() ?
      RendererMouseEvent.create(e.getX(), e.getY(), e.getButton(), MouseEventType.SCROLL, e.getUnitsToScroll() * 10, 0) :
      RendererMouseEvent.create(e.getX(), e.getY(), e.getButton(), MouseEventType.SCROLL, 0, e.getUnitsToScroll() * 10);
    if (rendererSupplier.get() instanceof EventForwardingTarget target) {
      target.forwardEvent(mouseEvent);
    }
  }
  
}
