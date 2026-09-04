package net.buildabrowser.babbrowser.renderer.event.handlers.textarea;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.events.FocusEvent;
import net.buildabrowser.babbrowser.html.html.RenderableDocument;
import net.buildabrowser.babbrowser.html.scripting.GlobalObject;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.GraphicalDocumentRenderer;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.VirtualKeyboard;
import net.buildabrowser.babbrowser.renderer.content.textarea.TextAreaContent;
import net.buildabrowser.babbrowser.renderer.event.ContentEventHandler;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.events.RendererKeyboardEvent;
import net.buildabrowser.babbrowser.renderer.event.handlers.common.TextEditContentEventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.input.OffThreadWriteTextController;
import net.buildabrowser.babbrowser.renderer.input.imp.OffThreadWriteTextControllerImp;

public class TextAreaContentEventHandler implements ContentEventHandler<TextAreaContent> {
  
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
    return TextEditContentEventHandler.handleKeyboardEvent(
      content.textController(), event,
      fontMetrics, contentWidth, contentHeight);
  }

  @Override
  public EventHandlerResponse handleElementEvent(
    ElementBox box,
    TextAreaContent content,
    Element target,
    Event event
  ) {
    if (
      event instanceof FocusEvent
      && event.type().equals("focus")
      && target.nodeDocument() instanceof RenderableDocument renderableDocument
      && renderableDocument.renderer() instanceof GraphicalDocumentRenderer graphicalRenderer
    ) {
      GlobalObject globalObject = renderableDocument.nodeNavigable().activeWindow();
      OffThreadWriteTextController controllerProxy
        = new OffThreadWriteTextControllerImp(content.textController(), globalObject);
      VirtualKeyboard virtualKeyboard = graphicalRenderer.frameAPIs().virtualKeyboard();
      virtualKeyboard.onInputConnected(controllerProxy);
      virtualKeyboard.show();
      return EventHandlerResponse.HANDLED;
    }

    return EventHandlerResponse.UNHANDLED;
  }

}
