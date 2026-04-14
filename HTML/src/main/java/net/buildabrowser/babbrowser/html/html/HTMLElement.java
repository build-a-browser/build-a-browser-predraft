package net.buildabrowser.babbrowser.html.html;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.Invalidatable;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Namespace;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.imp.HTMLElementImp;
import net.buildabrowser.babbrowser.html.navigation.Navigable;

public interface HTMLElement extends Element, Invalidatable {

  // Extensions
  // Unfortunately, the types needed cannot be accessed from here

  Object getContext();

  void setContext(Object context);
  
  Object getBox();

  void setBox(Object box);

  InvalidationLevel invalidationLevel();

  Navigable nodeNavigable();

  public static HTMLElement create(
    String name, Node parentNode
  ) {
    return new HTMLElementImp(name, Namespace.HTML_NAMESPACE, parentNode);
  }
  
}
