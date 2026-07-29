package net.buildabrowser.babbrowser.renderer.event.handlers.common;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.content.common.TextController;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent.MouseEventType;
import net.buildabrowser.babbrowser.renderer.event.util.MouseEventUtil;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.TextEditPainter;

public final class TextEditEventHandler {
  
  private TextEditEventHandler() {}

  public static EventHandlerResponse handleMouseEvent(
    TextController controller,
    FontMetrics fontMetrics,
    RendererMouseEvent mouseEvent, float relX, float relY
  ) {
    if (mouseEvent.event().equals(MouseEventType.CLICK)) {
      determineCursorX(controller, fontMetrics, relX);
    }

    return EventHandlerResponse.HANDLED;
  }

  private static void determineCursorX(
    TextController controller,
    FontMetrics fontMetrics,
    float relX
  ) {
    String value = controller.displayValue();
    float adjustedRelX = relX + controller.scrollX() - TextEditPainter.HORIZONTAL_PADDING;
    int cursorX = MouseEventUtil.determineTextMouseIndex(adjustedRelX, fontMetrics, value);
    controller.setCursorX(cursorX);
  }

}
