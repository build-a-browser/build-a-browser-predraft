package net.buildabrowser.babbrowser.cssbase.parser.imp;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizer;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class ActiveCSSTokenStream implements CSSTokenStream {
  
  private final CSSTokenizer cssTokenizer = CSSTokenizer.create();
  private final CSSTokenStreamSource source;
  private final CSSTokenizerInput tokenizerInput;

  private Token pushback = null;

  public ActiveCSSTokenStream(
    CSSTokenStreamSource source,
    CSSTokenizerInput tokenizerInput
  ) {
    this.source = source;
    this.tokenizerInput = tokenizerInput;
  }

  @Override
  public CSSTokenStreamSource source() {
    return this.source;
  }

  @Override
  public Token read() throws IOException {
    if (this.pushback != null) {
      Token rtn = this.pushback;
      this.pushback = null;
      return rtn;
    }

    return cssTokenizer.consumeAToken(tokenizerInput);
  }

  @Override
  public void unread(Token token) {
    this.pushback = token;
  }

}
