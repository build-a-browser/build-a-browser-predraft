package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class SelfClosingStartTagState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      // TODO: Other cases
      case '>':
        tokenizeContext.currentTagToken().setSelfClosing(true);
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
        parseContext.emitTagToken(tokenizeContext.currentTagToken());
        break;
      default:
        throw new UnsupportedOperationException("Not Implemented");
    }
  }

}
 