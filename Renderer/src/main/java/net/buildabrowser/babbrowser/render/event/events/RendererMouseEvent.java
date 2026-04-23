package net.buildabrowser.babbrowser.render.event.events;

public record RendererMouseEvent(
  float winX, float winY,
  int button, MouseEventType event,
  int scrollX, int scrollY
) {

  public static RendererMouseEvent create(
    float winX, float winY,
    int button, MouseEventType event
  ) {
    return new RendererMouseEvent(
      winX, winY, button, event, 0, 0);
  }

  public static RendererMouseEvent create(
    float winX, float winY,
    int button, MouseEventType event,
    int scrollX, int scrollY
  ) {
    return new RendererMouseEvent(
      winX, winY, button, event, scrollX, scrollY);
  }

  public static enum MouseEventType {
    CLICK, MOVE, SCROLL, DOWN, UP
  }
}