package net.buildabrowser.babbrowser.dom;

import java.util.Map;

import net.buildabrowser.babbrowser.dom.imp.ElementImp;

public interface Element extends Node {

  String name();

  String namespace();

  Map<String, String> attributes();

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
