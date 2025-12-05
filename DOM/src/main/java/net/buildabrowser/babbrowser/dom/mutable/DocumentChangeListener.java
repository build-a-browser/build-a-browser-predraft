package net.buildabrowser.babbrowser.dom.mutable;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public interface DocumentChangeListener {
  
  default void onNodeAdded(Node node) {}

  default void onNodeRemoved(Node node) {}

  default void onAttributeChanged(Element element, String attrName, String prevValue, String newValue) {}

  default void onStylesheetAdded(CSSStyleSheet styleSheet) {};

}
