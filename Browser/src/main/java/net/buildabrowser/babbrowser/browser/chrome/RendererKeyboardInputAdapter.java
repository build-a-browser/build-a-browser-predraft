package net.buildabrowser.babbrowser.browser.chrome;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.renderer.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.renderer.event.EventForwardingTarget;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent.KeyboardEventType;

public class RendererKeyboardInputAdapter implements KeyListener {

  private final Supplier<GraphicalDocumentRenderer> rendererSupplier;

  public RendererKeyboardInputAdapter(Supplier<GraphicalDocumentRenderer> rendererSupplier) {
    this.rendererSupplier = rendererSupplier;
  }

  @Override
  public void keyTyped(KeyEvent e) {
    fireKeyEvent(KeyboardEventType.KEY_PRESS, e);
  }

  @Override
  public void keyPressed(KeyEvent e) {
    fireKeyEvent(KeyboardEventType.KEY_UP, e);
  }

  @Override
  public void keyReleased(KeyEvent e) {
    fireKeyEvent(KeyboardEventType.KEY_DOWN, e);
  }

  private void fireKeyEvent(KeyboardEventType type, KeyEvent e) {
    RendererKeyboardEvent event = remapEvent(type, e);
    e.consume();

    EventForwardingTarget eventForwardingTarget = rendererSupplier.get().eventForwardingTarget();
    eventForwardingTarget.forwardEvent(event);
  }

  // TODO: More mappings, use physical keys
  private RendererKeyboardEvent remapEvent(
    KeyboardEventType type, KeyEvent e
  ) {
    String keyCode = switch (e.getKeyCode()) {
      case KeyEvent.VK_TAB -> RendererKeyboardEvent.KEY_TAB;
      case KeyEvent.VK_ENTER -> RendererKeyboardEvent.KEY_ENTER;
      case KeyEvent.VK_SPACE -> RendererKeyboardEvent.KEY_SPACE;
      default -> RendererKeyboardEvent.KEY_UNIDENTIFIED;
    };

    short modifiers = (short) (
      (e.isAltDown() ? RendererKeyboardEvent.MODIFIER_SHIFT : 0)
      + (e.isControlDown() ? RendererKeyboardEvent.MODIFIER_CTRL : 0)
      + (e.isMetaDown() ? RendererKeyboardEvent.MODIFIER_META : 0)
      + (e.isShiftDown() ? RendererKeyboardEvent.MODIFIER_SHIFT : 0));
    // TODO: Check repeat

    // TODO: Key name
    return new RendererKeyboardEvent("", keyCode, e.getKeyCode(), modifiers, type);
  }
  
}
