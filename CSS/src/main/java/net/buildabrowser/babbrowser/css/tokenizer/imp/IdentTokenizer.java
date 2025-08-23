package net.buildabrowser.babbrowser.css.tokenizer.imp;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.css.tokens.IdentToken;
import net.buildabrowser.babbrowser.css.tokens.Token;

public class IdentTokenizer {

  private final StringBuilder strBuilder = new StringBuilder();

  public Token consumeAnIdentLikeToken(CSSTokenizerInput stream) throws IOException {
    String result = consumeIdentSequence(stream);

    return IdentToken.create(result);
  }

  private String consumeIdentSequence(CSSTokenizerInput stream) throws IOException {
    strBuilder.setLength(0);

    int ch;
    while (TokenizerUtil.isIdentCodePoint(ch = stream.read())) {
      strBuilder.appendCodePoint(ch);
      // TODO: Consume an escape
    }
    stream.unread(ch);

    return strBuilder.toString();
  }

}
