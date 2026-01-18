package net.buildabrowser.babbrowser.cssbase.parser;

import java.io.IOException;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.CSSRuleList;
import net.buildabrowser.babbrowser.cssbase.cssom.CSSStyleSheet;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ActiveCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.imp.CSSParserImp;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public interface CSSParser {
  
  CSSStyleSheet parseAStyleSheet(CSSTokenStream tokenStream) throws IOException;

  CSSRuleList parseARuleList(CSSTokenStream tokenStream) throws IOException;

  List<Declaration> parseAStyleBlocksContents(CSSTokenStream tokenStream) throws IOException;

  static interface CSSTokenStream {
    
    Token read() throws IOException;

    void unread(Token token) throws IOException;

    default Token peek() throws IOException {
      Token result = read();
      unread(result);
      return result;
    }

    static CSSTokenStream create(CSSTokenizerInput input) {
      return new ActiveCSSTokenStream(input);
    }

    static SeekableCSSTokenStream create(List<Token> input) {
      return ListCSSTokenStream.create(input);
    }

    static SeekableCSSTokenStream create(Token... input) {
      return ListCSSTokenStream.create(input);
    }

  }

  static interface SeekableCSSTokenStream extends CSSTokenStream {
  
    int position();

    void seek(int position);
    
  }

  // Unfortunately, INSTANCE cannot be private due to interface rules
  static final CSSParser INSTANCE = new CSSParserImp();
  static CSSParser create() {
    return INSTANCE;
  }

}
