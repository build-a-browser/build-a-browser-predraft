package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class RCDataLessThanSignState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '/':
        tokenizeContext.temporaryBuffer().clear();
        tokenizeContext.setTokenizeState(TokenizeStates.RCDATA_END_TAG_OPEN_STATE);
        break;
      default:
        parseContext.emitCharacterToken('<');
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.RCDATA_STATE);
    }
  }
  
}
