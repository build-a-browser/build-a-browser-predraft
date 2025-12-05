package net.buildabrowser.babbrowser.dom.mutable.imp;

import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.dom.mutable.MutableDocument;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;

public class MutableElementImp extends MutableNodeImp implements MutableElement {

  private final Map<String, String> attributes = new HashMap<>();

  private final String name;
  private final String namespace;
  private final MutableDocument nodeDocument;

  public MutableElementImp(String name, String namespace, MutableDocument nodeDocument) {
    this.name = name;
    this.namespace = namespace;
    this.nodeDocument = nodeDocument;
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
  public MutableDocument ownerDocument() {
    return this.nodeDocument;
  }

  @Override
  public void addAttribute(String name, String value) {
    String prevValue = attributes.put(name, value);
    ownerDocument().changeListener().onAttributeChanged(this, name, prevValue, value);
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

}
