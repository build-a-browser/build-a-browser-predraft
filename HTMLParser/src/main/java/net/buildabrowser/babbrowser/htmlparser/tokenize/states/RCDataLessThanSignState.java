package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class RCDataLessThanSignState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    switch (ch) {
      case '/':
        tokenizeContext.temporaryBuffer().clear();
        tokenizeContext.setTokenizeState(TokenizeStates.rcdataEndTagOpenState);
        break;
      default:
        parseContext.emitCharacterToken('<');
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.rcdataState);
    }
  }
  
}
