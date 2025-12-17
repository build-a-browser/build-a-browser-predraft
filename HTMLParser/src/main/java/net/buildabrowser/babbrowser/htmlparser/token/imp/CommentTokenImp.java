package net.buildabrowser.babbrowser.htmlparser.token.imp;

import net.buildabrowser.babbrowser.htmlparser.token.CommentToken;

public class CommentTokenImp implements CommentToken {

  private final StringBuilder valueBuilder = new StringBuilder();
  
  @Override
  public String data() {
    return valueBuilder.toString();
  }

  @Override
  public void appendCodePointToData(int ch) {
    valueBuilder.appendCodePoint(ch);
  }

  public void reset() {
    valueBuilder.setLength(0);
  }
  
}
