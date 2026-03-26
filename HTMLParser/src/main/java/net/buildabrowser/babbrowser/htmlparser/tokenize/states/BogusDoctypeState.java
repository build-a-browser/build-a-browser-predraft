package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class BogusDoctypeState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '>':
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
        parseContext.emitDoctypeToken(tokenizeContext.currentDoctypeToken());
        break;
      case 0:
        parseContext.parseError();
        break;
      case TokenizeContext.EOF:
        parseContext.emitDoctypeToken(tokenizeContext.currentDoctypeToken());
        parseContext.emitEOFToken();
        break;
      default:
        break;
    }
  }
  
}
