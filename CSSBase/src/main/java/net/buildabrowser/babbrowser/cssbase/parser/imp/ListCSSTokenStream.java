package net.buildabrowser.babbrowser.cssbase.parser.imp;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class ListCSSTokenStream implements SeekableCSSTokenStream {

  private final List<Token> tokens;

  private Token unread;
  private int position = 0;

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
    if (position >= tokens.size()) {
      return EOFToken.create();
    }
    return tokens.get(position++);
  }

  @Override
  public void unread(Token token) {
    if (unread != null) {
      throw new UnsupportedOperationException("Exceeded max one unread token");
    }

    unread = token;
  }

  @Override
  public int position() {
    return this.position();
  }

  @Override
  public void seek(int position) {
    this.position = position;
  }

  public static SeekableCSSTokenStream create(List<Token> tokens) {
    return new ListCSSTokenStream(tokens);
  }

  public static SeekableCSSTokenStream create(Token... tokens) {
    return new ListCSSTokenStream(List.of(tokens));
  }
  
}
