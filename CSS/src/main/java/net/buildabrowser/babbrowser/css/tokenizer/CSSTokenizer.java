package net.buildabrowser.babbrowser.css.tokenizer;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.tokenizer.imp.CSSTokenizerImp;
import net.buildabrowser.babbrowser.css.tokens.Token;

public interface CSSTokenizer {

  Token consumeAToken(CSSTokenizerInput stream) throws IOException;

  static CSSTokenizer create() {
    return new CSSTokenizerImp();
  }

}
