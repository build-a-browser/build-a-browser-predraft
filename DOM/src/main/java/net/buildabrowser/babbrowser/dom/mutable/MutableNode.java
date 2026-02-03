package net.buildabrowser.babbrowser.dom.mutable;

import net.buildabrowser.babbrowser.dom.Node;

public interface MutableNode extends Node {

  MutableDocument ownerDocument();

  MutableNode firstChild();

  MutableNode lastChild();

  MutableNode nextSibling();

  MutableNode previousSibling();

  void setContext(Object context);

  Object getContext();

}
