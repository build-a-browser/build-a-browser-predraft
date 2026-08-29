package net.buildabrowser.babbrowser.cssbase.parser.imp;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
import net.buildabrowser.babbrowser.cssbase.tokens.WhitespaceToken;

public class ListCSSTokenStream implements CSSTokenStream {

  private final CSSTokenStreamSource source;
  private final List<Token> tokens;
  private final boolean skipWhitespace;

  private Token unread;
  private int position = 0;

  private ListCSSTokenStream(
    CSSTokenStreamSource source,
    List<Token> tokens,
    boolean skipWhitespace
  ) {
    this.source = source;
    this.tokens = tokens;
    this.skipWhitespace = skipWhitespace;
    if (tokens == null) throw new AssertionError();
  }

  @Override
  public CSSTokenStreamSource source() {
    return this.source;
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
    if (position > 0 && token == tokens.get(position - 1)) {
      position--;
      return;
    }

    unread = token;
  }

  //

  @Override
  public int mark() {
    return position;
  }

  @Override
  public void restoreMark(int mark) {
    this.position = mark;
    this.unread = null;
  }

  @Override
  public void discardMark() {}

  @Override
  public int nextMark() {
    return position;
  }

  @Override
  public void seek(int markRelPosition) {
    this.position = markRelPosition;
    this.unread = null;
  }

  public static CSSTokenStream create(
    CSSTokenStreamSource source, List<Token> tokens
  ) {
    return new ListCSSTokenStream(source, tokens, false);
  }

  public static CSSTokenStream create(
    CSSTokenStreamSource source, Token... tokens
  ) {
    return new ListCSSTokenStream(source, List.of(tokens), false);
  }

  public static CSSTokenStream createWithSkippedWhitespace(
    CSSTokenStreamSource source, List<Token> tokens
  ) {
    return new ListCSSTokenStream(source, tokens, true);
  }
  
}
