package net.buildabrowser.babbrowser.htmlparser.token;

import net.buildabrowser.babbrowser.htmlparser.token.imp.TagTokenImp;

public interface TagToken {

  boolean isStartTag();

  void appendToName(int ch);

  String name();

  static TagToken create(boolean isStartTag) {
    return new TagTokenImp(isStartTag);
  }

}
