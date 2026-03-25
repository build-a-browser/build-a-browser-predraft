package net.buildabrowser.babbrowser.dom.mutable;

import net.buildabrowser.babbrowser.dom.Node;

public interface MutableNode extends Node {

  MutableDocument ownerDocument();

  MutableNode firstChild();

  MutableNode lastChild();

  MutableNode nextSibling();

  MutableNode previousSibling();

  //

  Object getContext();

  void setContext(Object context);

  // Thanks again to not have type information here
  
  Object getBox();

  void setBox(Object box);

}
