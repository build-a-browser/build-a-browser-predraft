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
import net.buildabrowser.babbrowser.renderer.api.VirtualKeyboard;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;
import net.buildabrowser.babbrowser.renderer.event.EventContext;
import net.buildabrowser.babbrowser.renderer.event.EventUtil;

public class HTMLFocusManagerContext implements FocusManagerContext {

  private final EventContext eventContext;
  private final VirtualKeyboard virtualKeyboard;
  private final SlotFamily<HTMLElement, RenderContext> renderContexts;

  public HTMLFocusManagerContext(
    EventContext eventContext,
    VirtualKeyboard virtualKeyboard,
    SlotFamily<HTMLElement, RenderContext> renderContexts
  ) {
    this.eventContext = eventContext;
    this.virtualKeyboard = virtualKeyboard;
    this.renderContexts = renderContexts;
  }

  @Override
  public void onFocusChanged(Node oldFocused, Node newFocused) {
    virtualKeyboard.close();
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
    PropertyContainer properties = renderContexts.get(htmlElement).properties();
    OuterDisplayValue outerDisplay = PropertiesUtil.outerDisplayValue(properties);
    return switch (outerDisplay) {
      case CONTENTS -> FocusIgnore.SELF;
      case NONE -> FocusIgnore.TREE;
      default -> FocusIgnore.NONE;
    };
  }
  
}
