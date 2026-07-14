package net.buildabrowser.babbrowser.renderer.imp.html;

import net.buildabrowser.babbrowser.common.datastruct.SlotFamily;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.FocusEvent;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.html.input.FocusManagerContext;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;

public class HTMLFocusManagerContext implements FocusManagerContext {

  private final EventContext eventContext;
  private final SlotFamily<HTMLElement, ElementContext> elementContexts;

  public HTMLFocusManagerContext(
    EventContext eventContext,
    SlotFamily<HTMLElement, ElementContext> elementContexts
  ) {
    this.eventContext = eventContext;
    this.elementContexts = elementContexts;
  }

  @Override
  public void onFocusChanged(Node oldFocused, Node newFocused) {
    if (oldFocused instanceof Element element) {
      eventContext.setPreventDefault(false);
      EventUtil.forwardElementEvent(eventContext, (FocusEvent) () -> "blur", element);
    }
    if (newFocused instanceof Element element) {
      eventContext.setPreventDefault(false);
      EventUtil.forwardElementEvent(eventContext, (FocusEvent) () -> "focus", element);
    }
  }

  @Override
  public FocusIgnore getIgnore(Node node) {
    if (!(node instanceof HTMLElement htmlElement)) return FocusIgnore.NONE;
    PropertyContainer properties = elementContexts.get(htmlElement).properties();
    OuterDisplayValue outerDisplay = PropertiesUtil.outerDisplayValue(properties);
    return switch (outerDisplay) {
      case CONTENTS -> FocusIgnore.SELF;
      case NONE -> FocusIgnore.TREE;
      default -> FocusIgnore.NONE;
    };
  }
  
}
