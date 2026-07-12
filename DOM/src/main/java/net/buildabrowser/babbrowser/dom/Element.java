package net.buildabrowser.babbrowser.dom;

import java.util.List;

import net.buildabrowser.babbrowser.dom.imp.ElementImp;
import net.buildabrowser.babbrowser.infra.Namespace;

public interface Element extends Node {

  String name();

  String namespace();

  List<String> getAttributeNames();

  String getAttribute(String name);
  
  boolean hasAttribute(String name);

  void addAttribute(String name, String value);

  // Extensions

  int getId();

  void setId(int id);

  public static Element create(
    String name, Node parentNode
  ) {
    return new ElementImp(name, Namespace.HTML_NAMESPACE, parentNode);
  }

}
