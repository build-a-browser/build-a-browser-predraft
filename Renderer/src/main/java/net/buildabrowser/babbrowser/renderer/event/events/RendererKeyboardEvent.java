package net.buildabrowser.babbrowser.renderer.event.events;

import net.buildabrowser.babbrowser.dom.events.util.ModifierUtil;
import net.buildabrowser.babbrowser.renderer.event.EventForwardingTarget;

public record RendererKeyboardEvent(
  String key, String code, int keyCode, byte modifiers,
  KeyboardEventType type
) implements EventForwardingTarget {
  
  public static final String KEY_TAB = "Tab";
  public static final String KEY_SPACE = "Space";
  public static final String KEY_BACKSPACE = "Backspace";
  public static final String KEY_ENTER = "Enter";
  public static final String KEY_LEFT_ARROW = "ArrowLeft";
  public static final String KEY_RIGHT_ARROW = "ArrowRight";
  public static final String KEY_UP_ARROW = "ArrowUp";
  public static final String KEY_DOWN_ARROW = "ArrowDown";
  public static final String KEY_HOME = "Home";
  public static final String KEY_END = "End";
  public static final String KEY_DELETE = "Delete";
  public static final String KEY_INSERT = "Insert";
  public static final String KEY_PAGE_UP = "PageUp";
  public static final String KEY_PAGE_DOWN = "PageDown";
  public static final String KEY_UNIDENTIFIED = "Unidentified";

  public static final String KEY_C = "KeyC";

  public boolean altKey() {
    return (modifiers & ModifierUtil.MODIFIER_ALT) != 0;
  }

  public boolean ctrlKey() {
    return (modifiers & ModifierUtil.MODIFIER_CTRL) != 0;
  }

  public boolean metaKey() {
    return (modifiers & ModifierUtil.MODIFIER_META) != 0;
  }

  public boolean shiftKey() {
    return (modifiers & ModifierUtil.MODIFIER_SHIFT) != 0;
  }

  public static enum KeyboardEventType {
    KEY_PRESS, KEY_DOWN, KEY_UP
  }

}
