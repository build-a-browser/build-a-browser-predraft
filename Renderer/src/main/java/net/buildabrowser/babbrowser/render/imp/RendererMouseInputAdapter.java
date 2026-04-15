package net.buildabrowser.babbrowser.render.imp;

import java.util.function.Supplier;

import javax.swing.event.MouseInputAdapter;

import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.render.event.EventForwardingTarget;
import net.buildabrowser.babbrowser.render.event.EventHandler.MouseEvent;
import net.buildabrowser.babbrowser.render.event.EventHandler.MouseEvent.MouseEventType;

public class RendererMouseInputAdapter extends MouseInputAdapter {

  private final Supplier<DocumentRenderer> rendererSupplier;

  public RendererMouseInputAdapter(Supplier<DocumentRenderer> rendererSupplier) {
    this.rendererSupplier = rendererSupplier;
  }
  
  @Override
  public void mouseClicked(java.awt.event.MouseEvent e) {
    // TODO: Translate button
    MouseEvent mouseEvent = new MouseEvent(e.getX(), e.getY(), e.getButton(), MouseEventType.CLICK);
    if (rendererSupplier.get() instanceof EventForwardingTarget target) {
      target.forwardEvent(mouseEvent);
    }
  }

  @Override
  public void mouseMoved(java.awt.event.MouseEvent e) {
    // TODO: Translate button
    MouseEvent mouseEvent = new MouseEvent(e.getX(), e.getY(), e.getButton(), MouseEventType.MOVE);
    if (rendererSupplier.get() instanceof EventForwardingTarget target) {
      target.forwardEvent(mouseEvent);
    }
  }
  
}
