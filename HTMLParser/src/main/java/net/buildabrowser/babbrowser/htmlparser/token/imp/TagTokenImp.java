package net.buildabrowser.babbrowser.htmlparser.token.imp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class TagTokenImp implements TagToken {

  private final StringBuilder nameBuilder = new StringBuilder();
  private final List<AttributePair> attributes = new LinkedList<>();
  private final StringBuilder attributeNameBuilder = new StringBuilder();
  private final StringBuilder attributeValueBuilder = new StringBuilder();

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
      attributes.add(new AttributePair(
        attributeNameBuilder.toString(),
        attributeValueBuilder.toString()));
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
  public Map<String, String> attributes() {
    Map<String, String> attributesMap = new HashMap<>(2);
    for (AttributePair attrPair: attributes) {
      attributesMap.put(attrPair.name, attrPair.value());
    }

    if (attributeNameBuilder.length() != 0) {
      attributesMap.put(
        attributeNameBuilder.toString(),
        attributeValueBuilder.toString());
    }

    return attributesMap;
  }

  public void reinit(boolean isStartTag) {
    this.isStartTag = isStartTag;
    this.isSelfClosing = false;
    this.nameBuilder.setLength(0);
    attributes.clear();
    attributeNameBuilder.setLength(0);
    attributeValueBuilder.setLength(0);
  }

  private static record AttributePair(String name, String value) {}
  
}
