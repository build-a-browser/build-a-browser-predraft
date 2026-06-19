package net.buildabrowser.babbrowser.renderer.event.events;

import net.buildabrowser.babbrowser.renderer.event.EventForwardingTarget;

public record RendererKeyboardEvent(
  String key, String code, int keyCode, short modifiers,
  KeyboardEventType type
) implements EventForwardingTarget {
  
  public static String KEY_TAB = "Tab";
  public static String KEY_SPACE = "Space";
  public static String KEY_ENTER = "Enter";
  public static String KEY_UNIDENTIFIED = "Unidentified";

  public static short MODIFIER_ALT = 1 << 0;
  public static short MODIFIER_CTRL = 1 << 1;
  public static short MODIFIER_META = 1 << 2;
  public static short MODIFIER_SHIFT = 1 << 3;
  public static short MODIFIER_REPEAT = 1 << 4;

  public boolean shiftKey() {
    return (modifiers() & MODIFIER_SHIFT) != 0;
  }

  public static enum KeyboardEventType {
    KEY_PRESS, KEY_DOWN, KEY_UP
  }

}
