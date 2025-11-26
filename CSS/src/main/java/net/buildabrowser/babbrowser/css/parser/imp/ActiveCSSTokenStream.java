package net.buildabrowser.babbrowser.css.parser.imp;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.css.tokenizer.CSSTokenizer;
import net.buildabrowser.babbrowser.css.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.css.tokens.Token;

public class ActiveCSSTokenStream implements CSSTokenStream {
  
  private final CSSTokenizer cssTokenizer = CSSTokenizer.create();
  private final CSSTokenizerInput tokenizerInput;

  private Token pushback = null;

  public ActiveCSSTokenStream(CSSTokenizerInput tokenizerInput) {
    this.tokenizerInput = tokenizerInput;
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
