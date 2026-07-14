package net.buildabrowser.babbrowser.renderer.event.handlers.input;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.InputContent;
import net.buildabrowser.babbrowser.renderer.content.input.text.TextTypeContent;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent.MouseEventType;
import net.buildabrowser.babbrowser.renderer.event.util.MouseEventUtil;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.input.TextInputFragment;
import net.buildabrowser.babbrowser.renderer.paint.painters.input.TextInputBoxPainter;

public class TextInputEventHandler implements EventHandler<TextInputFragment> {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    TextInputFragment fragment, float relX, float relY
  ) {
    relX -= fragment.posX(Measurement.BORDER);
    relY -= fragment.posY(Measurement.BORDER);

    EventHandlerResponse response = EventUtil.forwardElementEvent(
      eventContext, mouseEvent, fragment, relX, relY);
    if (
      mouseEvent.event().equals(MouseEventType.CLICK)
      && !response.equals(EventHandlerResponse.HANDLED)
    ) {
      determineCursorX(fragment, relX);
    }

    return EventHandlerResponse.HANDLED;
  }

  private void determineCursorX(TextInputFragment fragment, float relX) {
    ElementBox box = fragment.box();
    FontMetrics fontMetrics = box.layoutContext().font().metrics();
    TextTypeContent content = ((InputContent) box.content()).innerContent(box);
    String value = content.displayValue();
    float adjustedRelX = relX + content.scrollX() - TextInputBoxPainter.HORIZONTAL_PADDING;
    int cursorX = MouseEventUtil.determineTextMouseIndex(adjustedRelX, fontMetrics, value);
    content.setCursorX(cursorX);
    box.context().invalidate(InvalidationLevel.PAINT);
  }
  
}
