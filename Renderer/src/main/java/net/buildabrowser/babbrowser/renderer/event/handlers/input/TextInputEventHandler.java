package net.buildabrowser.babbrowser.renderer.event.handlers.input;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.content.input.InputContent;
import net.buildabrowser.babbrowser.renderer.content.input.text.TextTypeContent;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent;
import net.buildabrowser.babbrowser.renderer.event.events.RendererMouseEvent.MouseEventType;
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

    EventHandlerResponse response = EventUtil.forwardElementEvent(mouseEvent, fragment, relX, relY);
    if (
      mouseEvent.event().equals(MouseEventType.CLICK)
      && !response.equals(EventHandlerResponse.HANDLED)
    ) {
      determineCursorX(fragment, relX);
    }

    return EventHandlerResponse.HANDLED;
  }

  private void determineCursorX(TextInputFragment fragment, float relX) {
    FontMetrics fontMetrics = fragment.box().layoutContext().font().metrics();
    TextTypeContent content = ((InputContent) fragment.box().content()).innerContent();
    String value = content.displayValue();
    int cursorX = 0;
    int charNum = 0;
    float adjustedRelX = relX + content.scrollX() - TextInputBoxPainter.HORIZONTAL_PADDING;
    while (
      charNum < value.length()
      // TODO: Bad performance, but if it was done character-by-character
      // then it might be thrown off by kerning
      && valueWidth(fontMetrics, value, charNum) / 2
        + valueWidth(fontMetrics, value, charNum + 1) / 2
        <= adjustedRelX
    ) {
      cursorX++;
      charNum += Character.charCount(value.codePointAt(charNum));
    }
    content.setCursorX(cursorX);
    fragment.box().context().invalidate(InvalidationLevel.PAINT);
  }

  private float valueWidth(FontMetrics fontMetrics, String value, int charNum) {
    return fontMetrics.stringWidth(value.substring(0, charNum));
  }
  
}
