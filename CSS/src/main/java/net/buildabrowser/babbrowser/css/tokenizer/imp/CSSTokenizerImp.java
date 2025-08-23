package net.buildabrowser.babbrowser.css.tokenizer.imp;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.tokenizer.CSSTokenizer;
import net.buildabrowser.babbrowser.css.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.css.tokens.ColonToken;
import net.buildabrowser.babbrowser.css.tokens.EOFToken;
import net.buildabrowser.babbrowser.css.tokens.LCBracketToken;
import net.buildabrowser.babbrowser.css.tokens.RCBracketToken;
import net.buildabrowser.babbrowser.css.tokens.SemicolonToken;
import net.buildabrowser.babbrowser.css.tokens.Token;
import net.buildabrowser.babbrowser.css.tokens.WhitespaceToken;

public class CSSTokenizerImp implements CSSTokenizer {

  private final IdentTokenizer identTokenizer = new IdentTokenizer();

  @Override
  public Token consumeAToken(CSSTokenizerInput stream) throws IOException {
    int ch = stream.read();
    return switch (ch) {
      case '\n', ' ', '\t' -> consumeWhitespace(stream);
      case ':' -> ColonToken.create();
      case ';' -> SemicolonToken.create();
      case '{' -> LCBracketToken.create();
      case '}' -> RCBracketToken.create();
      case -1 -> EOFToken.create();
      default -> handleOtherValue(stream, ch);
    };
  }

  private Token handleOtherValue(CSSTokenizerInput stream, int ch) throws IOException {
    if (TokenizerUtil.isIdentStartCodePoint(ch)) {
      stream.unread(ch);
      return identTokenizer.consumeAnIdentLikeToken(stream);
    }
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  private WhitespaceToken consumeWhitespace(CSSTokenizerInput stream) throws IOException {
    int ch;
    while ((ch = stream.read()) == '\n' || ch == ' ' || ch == '\t') {}
    stream.unread(ch);

    return WhitespaceToken.create();
  }
  
}
