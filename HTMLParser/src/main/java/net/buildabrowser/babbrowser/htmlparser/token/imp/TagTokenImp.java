package net.buildabrowser.babbrowser.htmlparser.token.imp;

import net.buildabrowser.babbrowser.htmlparser.token.TagToken;

public class TagTokenImp implements TagToken {

  private final StringBuilder nameBuilder = new StringBuilder();

  private boolean isStartTag;

  public TagTokenImp() {}

  public TagTokenImp(boolean isStartTag) {
    this.isStartTag = isStartTag;
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

  public void reinit(boolean isStartTag) {
    this.isStartTag = isStartTag;
    this.nameBuilder.setLength(0);
  }
  
}
