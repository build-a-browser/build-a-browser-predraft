package net.buildabrowser.babbrowser.cssbase.parser;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ActiveCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public interface CSSTokenStream {

  CSSTokenStreamSource source();
  
  Token read() throws IOException;

  void unread(Token token) throws IOException;

  default Token peek() throws IOException {
    Token result = read();
    unread(result);
    return result;
  }

  //

  int mark();

  void restoreMark(int mark);

  void discardMark();

  int nextMark();

  void seek(int markRelPosition);

  //

  static CSSTokenStream create(
    CSSTokenStreamSource source, CSSTokenizerInput input
  ) {
    return new ActiveCSSTokenStream(source, input);
  }

  static CSSTokenStream create(
    CSSTokenStreamSource source, List<Token> input
  ) {
    return ListCSSTokenStream.create(source, input);
  }

  static CSSTokenStream createForTesting(Token... input) {
    CSSTokenStreamSource source = new CSSTokenStreamSource(
      CommonUtil.rethrow(() -> new URI("about:blank")));
    return ListCSSTokenStream.create(source, input);
  }

}