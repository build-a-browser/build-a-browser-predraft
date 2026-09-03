package net.buildabrowser.babbrowser.renderer.imp.html;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;
import net.buildabrowser.babbrowser.dom.listener.AbstractDocumentChangeListener;
import net.buildabrowser.babbrowser.dom.listener.DocumentChangeListener;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.input.FocusManager;
import net.buildabrowser.babbrowser.html.input.FocusOptions;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;

public class HTMLEventDocumentChangeListener extends AbstractDocumentChangeListener {

  private final FocusManager focusManager;
  private final SlotFamily<HTMLElement, RenderContext> renderContexts;

  public HTMLEventDocumentChangeListener(
    HTMLDocument document,
    DocumentChangeListener nextListener,
    SlotFamily<HTMLElement, RenderContext> renderContexts
  ) {
    super(nextListener);
    this.focusManager = document.focusManager();
    this.renderContexts = renderContexts;
  }
  
  @Override
  public boolean onElementEvent(
    Element element, Event event, boolean allowDefault
  ) {
    if (event.type().equals("click")) {
      Element targetedElement = findFocusableElement(element);
      FocusOptions focusOptions = new FocusOptions();
      focusManager.focus(targetedElement, focusOptions);
    }

    if (
      allowDefault
      && element instanceof HTMLElement htmlElement
      && renderContexts.get(htmlElement) instanceof RenderContext renderContext
    ) {
      ElementBox box = renderContext.box();
      allowDefault = !box.content().withContentEventHandler(
        box, (eh, c) -> eh.handleElementEvent(htmlElement, event))
        .equals(EventHandlerResponse.HANDLED);
    }

    return super.onElementEvent(element, event, allowDefault);
  }

  private Element findFocusableElement(Node currentTarget) {
    while (currentTarget != null) {
      if (
        currentTarget instanceof HTMLElement element
        && element.tabIndex() >= 0
      ) return element;
      currentTarget = currentTarget.parentNode();
    }

    return null;
  }

}
