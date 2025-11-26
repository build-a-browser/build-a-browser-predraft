package net.buildabrowser.babbrowser.dom.mutable.imp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;
import net.buildabrowser.babbrowser.dom.mutable.MutableNode;

public class MutableElementImp implements MutableElement {

  private final List<Node> children = new LinkedList<>();
  private final Map<String, String> attributes = new HashMap<>();

  private final String name;

  private Object context;

  public MutableElementImp(String name) {
    this.name = name;
  }

  public MutableElementImp(String name, Map<String, String> attributes) {
    this.name = name;
    attributes.forEach((k, v) -> this.attributes.put(k, v));
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public List<Node> children() {
    return this.children;
  }

  @Override
  public Map<String, String> attributes() {
    return this.attributes;
  }

  @Override
  public void setContext(Object context) {
    this.context = context;
  }

  @Override
  public Object getContext() {
    return this.context;
  }

  @Override
  public void addAttribute(String name, String value) {
    attributes.put(name, value);
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("<");
    builder.append(name);
    for (Map.Entry<String, String> attributePairs: attributes.entrySet()) {
      builder.append(' ');
      builder.append(attributePairs.getKey());
      builder.append("=\"");
      builder.append(attributePairs.getValue());
      builder.append('"');
    }
    builder.append(">");
    for (Node child: children) {
      builder.append(child.toString());
    }
    builder
      .append("</")
      .append(name)
      .append(">");
    
    return builder.toString();
  }

  @Override
  public Element immutable() {
    return Element.create(
      name,
      children.stream().map(e -> e instanceof MutableNode n ? n.immutable() : e).toList(),
      attributes);
  }

}
