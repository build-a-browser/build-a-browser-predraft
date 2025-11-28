package net.buildabrowser.babbrowser.cssbase.parser.imp;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class ArrayCSSTokenStream implements CSSTokenStream {

  private final Token[] tokens;

  private Token unread;
  private int pos = 0;

  private ArrayCSSTokenStream(Token[] tokens) {
    this.tokens = tokens;
  }

  @Override
  public Token read() {
    if (unread != null) {
      Token nextToken = unread;
      unread = null;
      return nextToken;
    }
    if (pos >= tokens.length) {
      return EOFToken.create();
    }
    return tokens[pos++];
  }

  @Override
  public void unread(Token token) {
    if (unread != null) {
      throw new UnsupportedOperationException("Exceeded max one unread token");
    }

    unread = token;
  }
  
}
