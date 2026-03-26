package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class RCDataState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '&':
        tokenizeContext.setReturnState(this);
        tokenizeContext.setTokenizeState(TokenizeStates.characterReferenceState);
        break;
      case '<':
        tokenizeContext.setTokenizeState(TokenizeStates.rcdataLessThanSignState);
        break;
      case 0:
        parseContext.parseError();
        parseContext.emitCharacterToken(0xFFFD);
        break;
      case TokenizeContext.EOF:
        parseContext.emitEOFToken();
        break;
      default:
        parseContext.emitCharacterToken(ch);
        break;
    }
  }
  
}
