package net.buildabrowser.babbrowser.htmlparser.token.imp;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.imp.AttributeList;
import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class TagTokenImp implements TagToken {

  private final StringBuilder nameBuilder = new StringBuilder();
  private final StringBuilder attributeNameBuilder = new StringBuilder();
  private final StringBuilder attributeValueBuilder = new StringBuilder();

  private AttributeList attributes;
  private AttributeList lastAttribute;
  private boolean isStartTag;
  private boolean isSelfClosing;


  public TagTokenImp() {}

  public TagTokenImp(boolean isStartTag) {
    this.isStartTag = isStartTag;
  }

  public TagTokenImp(boolean isStartTag, String name) {
    this.isStartTag = isStartTag;
    nameBuilder.append(name);
  }

  @Override
  public boolean isStartTag() {
    return this.isStartTag;
  }

  @Override
  public void appendToName(int ch) {
    nameBuilder.appendCodePoint(ch);
  }

  @Override
  public String name() {
    return nameBuilder.toString();
  }

  @Override
  public void setSelfClosing(boolean isSelfClosing) {
    this.isSelfClosing = isSelfClosing;
  }

  @Override
  public boolean isSelfClosing() {
    return this.isSelfClosing;
  }

  @Override
  public void startNewAttribute() {
    if (attributeNameBuilder.length() != 0) {
      AttributeList newAttribute = new AttributeList(
        attributeNameBuilder.toString(),
        attributeValueBuilder.toString());
      if (this.attributes == null) {
        this.attributes = newAttribute;
      } else {
        IntrusiveList.add(lastAttribute, newAttribute);
      }
      this.lastAttribute = newAttribute;
    }

    attributeNameBuilder.setLength(0);
    attributeValueBuilder.setLength(0);
  }

  @Override
  public void appendToAttributeName(int ch) {
    attributeNameBuilder.appendCodePoint(ch);
  }

  @Override
  public void appendToAttributeValue(int ch) {
    attributeValueBuilder.appendCodePoint(ch);
  }

  @Override
  public void copyAttributesTo(Element element) {
    AttributeList currentAttribute = this.attributes;
    while (currentAttribute != null) {
      element.addAttribute(
        currentAttribute.name(),
        currentAttribute.value());
      currentAttribute = currentAttribute.next();
    }

    if (!attributeNameBuilder.isEmpty()) {
      element.addAttribute(
        attributeNameBuilder.toString(),
      attributeValueBuilder.toString());
    }
  }

  public void reinit(boolean isStartTag) {
    this.isStartTag = isStartTag;
    this.isSelfClosing = false;
    this.nameBuilder.setLength(0);
    this.attributes = this.lastAttribute = null;
    attributeNameBuilder.setLength(0);
    attributeValueBuilder.setLength(0);
  }
  
}
