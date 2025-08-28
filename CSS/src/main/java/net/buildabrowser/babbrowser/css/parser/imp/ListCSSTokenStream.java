package net.buildabrowser.babbrowser.css.parser.imp;

import java.util.List;

import net.buildabrowser.babbrowser.css.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.css.tokens.EOFToken;
import net.buildabrowser.babbrowser.css.tokens.Token;

public class ListCSSTokenStream implements CSSTokenStream {

  private final List<Token> tokens;

  private Token unread;
  private int pos = 0;

  private ListCSSTokenStream(List<Token> tokens) {
    this.tokens = tokens;
  }

  @Override
  public Token read() {
    if (unread != null) {
      Token nextToken = unread;
      unread = null;
      return nextToken;
    }
    if (pos >= tokens.size()) {
      return EOFToken.create();
    }
    return tokens.get(pos++);
  }

  @Override
  public void unread(Token token) {
    if (unread != null) {
      throw new UnsupportedOperationException("Exceeded max one unread token");
    }

    unread = token;
  }

  public static ListCSSTokenStream create(List<Token> tokens) {
    return new ListCSSTokenStream(tokens);
  }
  
}
