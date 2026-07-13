package net.buildabrowser.babbrowser.dom.listener;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.events.Event;

public interface DocumentChangeListener {
  
  default void onNodeAdded(Node node) {}

  default void onNodeRemoved(Node node) {}

  default void onAttributeChanged(Element element, String attrName, String prevValue, String newValue) {}

  default void onStylesheetAdded(CSSStyleSheet styleSheet) {}

  default boolean onElementEvent(
    Element element, Event event, boolean allowDefault
  ) {
    return allowDefault;
  }

}
