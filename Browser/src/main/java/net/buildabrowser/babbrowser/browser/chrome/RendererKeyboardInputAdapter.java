package net.buildabrowser.babbrowser.browser.chrome;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Supplier;

import net.buildabrowser.babbrowser.dom.events.util.ModifierUtil;
import net.buildabrowser.babbrowser.renderer.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.renderer.event.EventForwardingTarget;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent.KeyboardEventType;

public class RendererKeyboardInputAdapter implements KeyListener {

  private final Supplier<GraphicalDocumentRenderer> rendererSupplier;

  public RendererKeyboardInputAdapter(Supplier<GraphicalDocumentRenderer> rendererSupplier) {
    this.rendererSupplier = rendererSupplier;
  }

  @Override
  public void keyTyped(KeyEvent e) {
    if (Character.isISOControl(e.getKeyChar())) return;
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
    eventForwardingTarget.forwardEvent(event, EventHandlerResponse.UNHANDLED);
  }

  // TODO: More mappings, use physical keys
  // TODO: Repeat backspace event
  private RendererKeyboardEvent remapEvent(
    KeyboardEventType type, KeyEvent e
  ) {
    String keyCode = switch (e.getKeyCode()) {
      case KeyEvent.VK_TAB -> RendererKeyboardEvent.KEY_TAB;
      case KeyEvent.VK_ENTER -> RendererKeyboardEvent.KEY_ENTER;
      case KeyEvent.VK_SPACE -> RendererKeyboardEvent.KEY_SPACE;
      case KeyEvent.VK_BACK_SPACE -> RendererKeyboardEvent.KEY_BACKSPACE;
      case KeyEvent.VK_LEFT -> RendererKeyboardEvent.KEY_LEFT_ARROW;
      case KeyEvent.VK_RIGHT -> RendererKeyboardEvent.KEY_RIGHT_ARROW;
      case KeyEvent.VK_UP -> RendererKeyboardEvent.KEY_UP_ARROW;
      case KeyEvent.VK_DOWN -> RendererKeyboardEvent.KEY_DOWN_ARROW;
      case KeyEvent.VK_HOME -> RendererKeyboardEvent.KEY_HOME;
      case KeyEvent.VK_END -> RendererKeyboardEvent.KEY_END;
      case KeyEvent.VK_DELETE -> RendererKeyboardEvent.KEY_DELETE;
      case KeyEvent.VK_INSERT -> RendererKeyboardEvent.KEY_INSERT;
      case KeyEvent.VK_PAGE_UP -> RendererKeyboardEvent.KEY_PAGE_UP;
      case KeyEvent.VK_PAGE_DOWN -> RendererKeyboardEvent.KEY_PAGE_DOWN;

      case KeyEvent.VK_C -> RendererKeyboardEvent.KEY_C;
      default -> RendererKeyboardEvent.KEY_UNIDENTIFIED;
    };

    byte modifiers = getModifiers(e);

    // TODO: Use the proper name for non-char characters
    char keyChar = e.getKeyChar();
    String keyName = keyChar == KeyEvent.CHAR_UNDEFINED ?
      "" :
      Character.toString(keyChar);
    return new RendererKeyboardEvent(keyName, keyCode, e.getKeyCode(), modifiers, type);
  }

  public static byte getModifiers(InputEvent e) {
    byte modifiers = (byte) (
      (e.isAltDown() ? ModifierUtil.MODIFIER_SHIFT : 0)
      + (e.isControlDown() ? ModifierUtil.MODIFIER_CTRL : 0)
      + (e.isMetaDown() ? ModifierUtil.MODIFIER_META : 0)
      + (e.isShiftDown() ? ModifierUtil.MODIFIER_SHIFT : 0));
    // TODO: Check repeat
    return modifiers;
  }
  
}
