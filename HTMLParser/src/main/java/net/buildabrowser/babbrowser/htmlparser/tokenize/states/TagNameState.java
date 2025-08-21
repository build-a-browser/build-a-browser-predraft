package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;

public class TagNameState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '>':
        tokenizeContext.setTokenizeState(new DataState());
        parseContext.emitTagToken(tokenizeContext.currentTagToken());
        break;
      default:
        tokenizeContext.currentTagToken().appendToName(ch);
        break;
    }
  }

}
