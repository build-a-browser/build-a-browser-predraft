package net.buildabrowser.babbrowser.dom.imp;

import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public class ElementImp extends NodeImp implements Element {

  private final Map<String, String> attributes = new HashMap<>(2);

  private final String name;
  private final String namespace;

  private int id = -1;

  public ElementImp(String name, String namespace, Node parentNode) {
    this.name = name;
    this.namespace = namespace;
    this.parentNode = parentNode;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public String namespace() {
    return this.namespace;
  }

  @Override
  public Map<String, String> attributes() {
    return this.attributes;
  }

  @Override
  public void addAttribute(String name, String value) {
    String prevValue = attributes.put(name, value);
    nodeDocument().changeListener().onAttributeChanged(this, name, prevValue, value);
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
    for (Node child: childNodes()) {
      builder.append(child.toString());
    }
    builder
      .append("</")
      .append(name)
      .append(">");
    
    return builder.toString();
  }

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

}
