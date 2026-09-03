package net.buildabrowser.babbrowser.renderer.event.handlers.common;

import java.util.List;

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
      determineTextCursor(controller, fontMetrics, relX, relY);
      return EventHandlerResponse.HANDLED;
    }

    return EventHandlerResponse.UNHANDLED;
  }

  private static void determineTextCursor(
    TextController controller,
    FontMetrics fontMetrics,
    float relX, float relY
  ) {
    List<String> lines = controller.displayLines();
    float adjustedRelX = relX + controller.scrollX() - TextEditPainter.HORIZONTAL_PADDING;
    float adjustedRelY = relY;
    int cursorY = controller.isMultiLine() ? (int) (adjustedRelY / fontMetrics.height()) : 0;
    if (cursorY >= lines.size()) {
      cursorY = lines.size() - 1;
    }
    int cursorX = MouseEventUtil.determineTextMouseIndex(adjustedRelX, fontMetrics, lines.get(cursorY));
    controller.setCursorX(cursorX);
    controller.setCursorY(cursorY);
  }

}
