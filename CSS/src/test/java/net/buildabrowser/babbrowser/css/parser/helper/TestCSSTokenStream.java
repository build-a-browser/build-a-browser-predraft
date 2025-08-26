package net.buildabrowser.babbrowser.css.parser.helper;

import net.buildabrowser.babbrowser.css.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.css.tokens.Token;

public class TestCSSTokenStream implements CSSTokenStream {

  private final Token[] tokens;

  private Token unread;
  private int pos = 0;

  private TestCSSTokenStream(Token[] tokens) {
    this.tokens = tokens;
  }

  @Override
  public Token read() {
    if (unread != null) {
      Token nextToken = unread;
      unread = null;
      return nextToken;
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

  public static TestCSSTokenStream create(Token... tokens) {
    return new TestCSSTokenStream(tokens);
  }
  
}
