package net.buildabrowser.babbrowser.dom.imp;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;

public class AttributeList implements IntrusiveList<AttributeList> {

  private final String name;

  private AttributeList next;
  private String value;

  public AttributeList(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String name() {
    return this.name;
  }

  public String value() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public AttributeList next() {
    return this.next;
  }

  @Override
  public void setNext(AttributeList nextNode) {
    this.next = nextNode;
  }
  
}
