package net.buildabrowser.babbrowser.dom.imp;

import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.Node;

public class ElementImp extends NodeImp implements Element {

  private final String name;
  private final String namespace;

  private int id = -1;
  private AttributeList attributes;

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
  public List<String> getAttributeNames() {
    List<String> names = new ArrayList<>();
    AttributeList currentAttribute = attributes;
    while (currentAttribute != null) {
      names.add(currentAttribute.name());
      currentAttribute = currentAttribute.next();
    }

    return names;
  }

  @Override
  public String getAttribute(String name) {
    AttributeList currentAttribute = attributes;
    while (currentAttribute != null) {
      if (currentAttribute.name().equals(name)) {
        return currentAttribute.value();
      }
      currentAttribute = currentAttribute.next();
    }

    return null;
  }

  @Override
  public boolean hasAttribute(String name) {
    return getAttribute(name) != null;
  }

  @Override
  public void addAttribute(String name, String value) {
    AttributeList currentAttribute = attributes;
    String prevValue = null;
    while (currentAttribute != null) {
      if (currentAttribute.name().equals(name)) {
        prevValue = currentAttribute.value();
        currentAttribute.setValue(value);
        break;
      }
      currentAttribute = currentAttribute.next();
    }

    if (prevValue == null) {
      AttributeList attribute = new AttributeList(name, value);
      attribute.setNext(attributes);
      this.attributes = attribute;
    }

    nodeDocument().changeListener().onAttributeChanged(this, name, prevValue, value);
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
