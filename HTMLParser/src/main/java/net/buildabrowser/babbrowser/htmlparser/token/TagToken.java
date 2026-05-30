package net.buildabrowser.babbrowser.htmlparser.token;

import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.htmlparser.token.imp.TagTokenImp;

public interface TagToken {

  boolean isStartTag();

  void appendToName(int ch);

  String name();

  void setSelfClosing(boolean b);

  boolean isSelfClosing();

  void startNewAttribute();

  // TODO: Attribute object?
  void appendToAttributeName(int ch);

  void appendToAttributeValue(int ch);

  void copyAttributesTo(Element element);

  String attribute(String string);

  void acknowledgeSelfClosingFlag();

  static TagToken create(boolean isStartTag) {
    return new TagTokenImp(isStartTag);
  }

  static TagToken create(boolean isStartTag, String name) {
    return new TagTokenImp(isStartTag, name);
  }

}
