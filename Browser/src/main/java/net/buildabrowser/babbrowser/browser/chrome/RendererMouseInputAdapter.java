package net.buildabrowser.babbrowser.browser.chrome;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.function.Supplier;

import javax.swing.event.MouseInputAdapter;

import net.buildabrowser.babbrowser.renderer.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.renderer.event.EventForwardingTarget;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent.MouseEventType;

public class RendererMouseInputAdapter extends MouseInputAdapter {

  private static final int SCROLL_AMOUNT = 20;

  private final Supplier<GraphicalDocumentRenderer> rendererSupplier;

  // TODO: Should store each button
  private boolean mouseDown = false;

  private final Component panel;

  public RendererMouseInputAdapter(
    Component panel,
    Supplier<GraphicalDocumentRenderer> rendererSupplier
  ) {
    this.panel = panel;
    this.rendererSupplier = rendererSupplier;
  }
  
  @Override
  public void mouseClicked(MouseEvent e) {
    panel.requestFocusInWindow();
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
    panel.requestFocusInWindow();
    handleGeneric(e, MouseEventType.MOVE);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    this.mouseDown = true;
    panel.requestFocusInWindow();
    handleGeneric(e, MouseEventType.DOWN);
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    this.mouseDown = false;
    panel.requestFocusInWindow();
    handleGeneric(e, MouseEventType.UP);
  }

  public void mouseWheelMoved(MouseWheelEvent e) {
    e.consume();
    byte modifiers = RendererKeyboardInputAdapter.getModifiers(e);
    // TODO: Some mice (not mine) have horizontal scroll wheels, how to detect that?
    RendererMouseEvent mouseEvent = e.isShiftDown() ?
      RendererMouseEvent.create(
        e.getX(), e.getY(), e.getButton(),
        MouseEventType.SCROLL, e.getUnitsToScroll() * SCROLL_AMOUNT, 0,
        modifiers) :
      RendererMouseEvent.create(
        e.getX(), e.getY(), e.getButton(),
        MouseEventType.SCROLL, 0, e.getUnitsToScroll() * SCROLL_AMOUNT,
        modifiers);
    EventForwardingTarget target = rendererSupplier.get().eventForwardingTarget();
    target.forwardEvent(mouseEvent, EventHandlerResponse.UNHANDLED);
  }

  private void handleGeneric(MouseEvent e, MouseEventType type) {
    // TODO: Translate button
    e.consume();
    byte modifiers = RendererKeyboardInputAdapter.getModifiers(e);
    RendererMouseEvent mouseEvent = RendererMouseEvent.create(
      e.getX(), e.getY(), e.getButton(), type, modifiers);
    EventForwardingTarget target = rendererSupplier.get().eventForwardingTarget();
    target.forwardEvent(mouseEvent, EventHandlerResponse.UNHANDLED);
  }
  
}
