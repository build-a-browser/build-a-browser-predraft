package net.buildabrowser.babbrowser.htmlparser.tokenize;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;

public interface TokenizeState {
  
  void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException;

}
