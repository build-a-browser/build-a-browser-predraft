package net.buildabrowser.babbrowser.cssbase.parser.imp;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
import net.buildabrowser.babbrowser.cssbase.tokens.WhitespaceToken;

public class ListCSSTokenStream implements SeekableCSSTokenStream {

  private final List<Token> tokens;
  private final boolean skipWhitespace;

  private Token unread;
  private int position = 0;

  private ListCSSTokenStream(List<Token> tokens, boolean skipWhitespace) {
    this.tokens = tokens;
    this.skipWhitespace = skipWhitespace;
  }

  @Override
  public Token read() {
    if (unread != null) {
      Token nextToken = unread;
      unread = null;
      return nextToken;
    }
    while (
      skipWhitespace
      && position < tokens.size()
      && tokens.get(position) instanceof WhitespaceToken
    ) {
      position++;
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
    if (token == tokens.get(position - 1)) {
      position--;
      return;
    }

    unread = token;
  }

  @Override
  public int position() {
    return this.position;
  }

  @Override
  public void seek(int position) {
    this.position = position;
    this.unread = null;
  }

  public static SeekableCSSTokenStream create(List<Token> tokens) {
    return new ListCSSTokenStream(tokens, false);
  }

  public static SeekableCSSTokenStream create(Token... tokens) {
    return new ListCSSTokenStream(List.of(tokens), false);
  }

  public static SeekableCSSTokenStream createWithSkippedWhitespace(List<Token> tokens) {
    return new ListCSSTokenStream(tokens, true);
  }
  
}
