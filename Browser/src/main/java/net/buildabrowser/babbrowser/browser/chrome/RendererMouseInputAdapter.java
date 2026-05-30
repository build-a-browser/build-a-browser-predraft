package net.buildabrowser.babbrowser.browser.chrome;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.function.Supplier;

import javax.swing.event.MouseInputAdapter;

import net.buildabrowser.babbrowser.html.navigation.DocumentRenderer;
import net.buildabrowser.babbrowser.render.event.EventForwardingTarget;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.render.event.events.RendererMouseEvent.MouseEventType;

public class RendererMouseInputAdapter extends MouseInputAdapter {

  private static final int SCROLL_AMOUNT = 20;

  private final Supplier<DocumentRenderer> rendererSupplier;

  // TODO: Should store each button
  private boolean mouseDown = false;

  public RendererMouseInputAdapter(Supplier<DocumentRenderer> rendererSupplier) {
    this.rendererSupplier = rendererSupplier;
  }
  
  @Override
  public void mouseClicked(MouseEvent e) {
    handleGeneric(e, MouseEventType.CLICK);
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    if (mouseDown) {
      mouseReleased(e);
    }
    handleGeneric(e, MouseEventType.MOVE);
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    this.mouseDown = true;
    handleGeneric(e, MouseEventType.MOVE);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    this.mouseDown = true;
    handleGeneric(e, MouseEventType.DOWN);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    this.mouseDown = false;
    handleGeneric(e, MouseEventType.UP);
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    e.consume();
    // TODO: Some mice (not mine) have horizontal scroll wheels, how to detect that?
    RendererMouseEvent mouseEvent = e.isShiftDown() ?
      RendererMouseEvent.create(e.getX(), e.getY(), e.getButton(), MouseEventType.SCROLL, e.getUnitsToScroll() * SCROLL_AMOUNT, 0) :
      RendererMouseEvent.create(e.getX(), e.getY(), e.getButton(), MouseEventType.SCROLL, 0, e.getUnitsToScroll() * SCROLL_AMOUNT);
    if (rendererSupplier.get() instanceof EventForwardingTarget target) {
      target.forwardEvent(mouseEvent);
    }
  }

  private void handleGeneric(MouseEvent e, MouseEventType type) {
    // TODO: Translate button
    e.consume();
    RendererMouseEvent mouseEvent = RendererMouseEvent.create(e.getX(), e.getY(), e.getButton(), type);
    if (rendererSupplier.get() instanceof EventForwardingTarget target) {
      target.forwardEvent(mouseEvent);
    }
  }
  
}
