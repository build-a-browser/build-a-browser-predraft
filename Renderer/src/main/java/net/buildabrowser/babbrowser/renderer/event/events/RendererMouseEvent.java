package net.buildabrowser.babbrowser.renderer.event.events;

public record RendererMouseEvent(
  float winX, float winY,
  int button, MouseEventType event,
  int scrollX, int scrollY,
  byte modifiers
) implements RendererEvent {

  public static RendererMouseEvent create(
    float winX, float winY,
    int button, MouseEventType event,
    byte modifiers
  ) {
    return new RendererMouseEvent(
      winX, winY, button, event, 0, 0, modifiers);
  }

  public static RendererMouseEvent create(
    float winX, float winY,
    int button, MouseEventType event,
    int scrollX, int scrollY,
    byte modifiers
  ) {
    return new RendererMouseEvent(
      winX, winY, button, event, scrollX, scrollY, modifiers);
  }

  public static enum MouseEventType {
    CLICK, MOVE, SCROLL, DOWN, UP
  }
}