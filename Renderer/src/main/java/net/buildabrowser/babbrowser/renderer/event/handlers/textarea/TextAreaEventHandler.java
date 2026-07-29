package net.buildabrowser.babbrowser.renderer.event.handlers.textarea;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.textarea.TextAreaContent;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.event.handlers.common.TextEditEventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.textarea.TextAreaBoxFragment;

public class TextAreaEventHandler implements EventHandler<TextAreaBoxFragment> {

  @Override
  public EventHandlerResponse handleMouseEvent(
    EventContext eventContext, RendererMouseEvent mouseEvent,
    TextAreaBoxFragment fragment, float relX, float relY
  ) {
    relX -= fragment.posX(Measurement.BORDER);
    relY -= fragment.posY(Measurement.BORDER);

    EventHandlerResponse response = EventUtil.forwardElementEvent(
      eventContext, mouseEvent, fragment, relX, relY);
    if (!response.equals(EventHandlerResponse.HANDLED)) {
      ElementBox box = fragment.box();
      FontMetrics fontMetrics = box.layoutContext().font().metrics();
      TextAreaContent content = (TextAreaContent) box.content();
      EventHandlerResponse innerResponse = TextEditEventHandler.handleMouseEvent(
        content.textController(), fontMetrics, mouseEvent, relX, relY);
      if (innerResponse.equals(EventHandlerResponse.HANDLED)) {
        box.context().invalidate(InvalidationLevel.PAINT);
        return EventHandlerResponse.HANDLED;
      }
    }

    return response;
  }
  
}
