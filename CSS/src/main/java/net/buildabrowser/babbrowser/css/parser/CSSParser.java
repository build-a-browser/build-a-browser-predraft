package net.buildabrowser.babbrowser.css.parser;

import java.io.IOException;
import java.util.List;

import net.buildabrowser.babbrowser.css.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.css.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.css.cssom.Declaration;
import net.buildabrowser.babbrowser.css.parser.imp.ActiveCSSTokenStream;
import net.buildabrowser.babbrowser.css.parser.imp.CSSParserImp;
import net.buildabrowser.babbrowser.css.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.css.tokens.Token;

public interface CSSParser {
  
  CSSStyleSheet parseAStyleSheet(CSSTokenStream tokenStream) throws IOException;

  CSSRuleList parseARuleList(CSSTokenStream tokenStream) throws IOException;

  List<Declaration> parseAStyleBlocksContents(CSSTokenStream tokenStream) throws IOException;

  static interface CSSTokenStream {
    
    Token read() throws IOException;

    void unread(Token token);

    static CSSTokenStream create(CSSTokenizerInput input) {
      return new ActiveCSSTokenStream(input);
    }

  }

  static CSSParser create() {
    return new CSSParserImp();
  }

}
