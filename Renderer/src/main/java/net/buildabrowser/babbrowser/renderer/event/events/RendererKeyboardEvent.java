package net.buildabrowser.babbrowser.renderer.event.events;

import net.buildabrowser.babbrowser.renderer.event.EventForwardingTarget;

public record RendererKeyboardEvent(
  String key, String code, int keyCode, short modifiers,
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

  public static final short MODIFIER_ALT = 1 << 0;
  public static final short MODIFIER_CTRL = 1 << 1;
  public static final short MODIFIER_META = 1 << 2;
  public static final short MODIFIER_SHIFT = 1 << 3;
  public static final short MODIFIER_REPEAT = 1 << 4;

  public boolean ctrlKey() {
    return (modifiers() & MODIFIER_CTRL) != 0;
  }

  public boolean shiftKey() {
    return (modifiers() & MODIFIER_SHIFT) != 0;
  }

  public static enum KeyboardEventType {
    KEY_PRESS, KEY_DOWN, KEY_UP
  }

}
