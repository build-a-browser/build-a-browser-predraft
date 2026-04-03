package net.buildabrowser.babbrowser.dom;

import net.buildabrowser.babbrowser.dom.events.EventTarget;

public interface Node extends EventTarget {
  
  Document nodeDocument();

  Node parentNode();

  NodeList childNodes();

  Node firstChild();

  Node lastChild();

  Node nextSibling();

  Node previousSibling();

  Node appendChild(Node node);

  default EventTarget getTheParent() {
    return parentNode();
  }

}
