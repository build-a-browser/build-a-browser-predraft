package net.buildabrowser.babbrowser.fetch.imp;

import net.buildabrowser.babbrowser.fetch.HeaderList.Header;

public class HeaderImp implements Header {

  private final String name;
  private final String value;

  private Header next;

  public HeaderImp(String name, String value) {
    this.name = name;
    this.value = value;
  }

  @Override
  public String name() {
    return this.name;
  }

  @Override
  public String value() {
    return this.value;
  }

  @Override
  public Header next() {
    return this.next;
  }

  @Override
  public void setNext(Header nextNode) {
    this.next = nextNode;
  }
  
}
