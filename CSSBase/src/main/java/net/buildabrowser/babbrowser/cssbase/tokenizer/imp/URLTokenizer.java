package net.buildabrowser.babbrowser.cssbase.tokenizer.imp;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.cssbase.tokens.BadURLToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
import net.buildabrowser.babbrowser.cssbase.tokens.URLToken;

public class URLTokenizer {

  private IdentTokenizer identTokenizer;

  public URLTokenizer(IdentTokenizer identTokenizer) {
    this.identTokenizer = identTokenizer;
  }

  public Token consumeURLToken(CSSTokenizerInput stream) throws IOException {
    StringBuilder value = new StringBuilder();
    int ch = stream.peek();
    while (TokenizerUtil.isWhiteSpace(ch)) {
      stream.read();
      ch = stream.peek();
    }

    while (true) {
      ch = stream.read();
      switch (ch) {
      case ')':
        return URLToken.create(value.toString());
      case -1:
        // TODO: Parse error
        return URLToken.create(value.toString());
      case '\n', '\t', ' ':
        ch = stream.peek();
        while (ch == '\n' || ch == '\t' || ch == ' ') {
          stream.read();
          ch = stream.peek();
        }
        if (ch == ')') {
          stream.read();
          return URLToken.create(value.toString());
        } else if (ch == -1) {
          // TODO: Parse error
          return URLToken.create(value.toString());
        } else {
          consumeRemnantsOfBadURL(stream);
          return BadURLToken.create();
        }
      case '"', '\'', '(':
        // TODO: Parse error
        consumeRemnantsOfBadURL(stream);
        return BadURLToken.create();
      case '\\':
        if (TokenizerUtil.isValidEscape(stream)) {
          ch = identTokenizer.consumeAnEscapedCodepoint(stream);
          value.appendCodePoint(ch);
          break;
        } else {
          // TODO: Parse error
          consumeRemnantsOfBadURL(stream);
          return BadURLToken.create();
        }
      default:
        value.appendCodePoint(ch);
        break;
      }
    }
  }

  private void consumeRemnantsOfBadURL(CSSTokenizerInput stream) throws IOException {
    while (true) {
      int ch = stream.read();
      if (ch == ')') {
        return;
      } else if (TokenizerUtil.isValidEscape(stream)) {
        identTokenizer.consumeAnEscapedCodepoint(stream);
      }
    }
  }

}
