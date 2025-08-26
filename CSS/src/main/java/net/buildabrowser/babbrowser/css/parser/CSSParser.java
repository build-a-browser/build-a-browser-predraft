package net.buildabrowser.babbrowser.css.parser;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.css.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.css.tokens.Token;

public interface CSSParser {
  
  CSSStyleSheet parseAStyleSheet(CSSTokenStream tokenStream) throws IOException;

  CSSRuleList parseARuleList(CSSTokenStream tokenStream) throws IOException;

  static interface CSSTokenStream {
    
    Token read() throws IOException;

    void unread(Token token);

  }

}
