package net.buildabrowser.babbrowser.renderer.event.handlers.textarea;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.textarea.TextAreaContent;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.FocusEventHandler;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent;
import net.buildabrowser.babbrowser.renderer.event.handlers.common.TextEditFocusEventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;

public class TextAreaFocusEventHandler implements FocusEventHandler<TextAreaContent> {
  
  @Override
  public EventHandlerResponse handleKeyboardEvent(
    EventContext eventContext,
    ElementBox box,
    TextAreaContent content,
    RendererKeyboardEvent event
  ) {
    // TODO: Support more keys like ctrl, insert, and support text selection
    box.context().invalidate(InvalidationLevel.PAINT);

    BoxFragment<?> fragment = box.positioningFragment();
    assert fragment != null;
    float contentWidth = fragment == null ? 0 : fragment.width(Measurement.CONTENT);
    float contentHeight = fragment == null ? 0 : fragment.height(Measurement.CONTENT);
    FontMetrics fontMetrics = box.layoutContext().font().metrics();
    return TextEditFocusEventHandler.handleKeyboardEvent(
      content.textController(), event,
      fontMetrics, contentWidth, contentHeight);
  }

}
