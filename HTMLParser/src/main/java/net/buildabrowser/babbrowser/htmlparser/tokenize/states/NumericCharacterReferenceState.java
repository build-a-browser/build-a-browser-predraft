package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class NumericCharacterReferenceState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    tokenizeContext.setCharacterReferenceCode(0);
    switch (ch) {
      case 'x', 'X':
        tokenizeContext.temporaryBuffer().append(ch);
        tokenizeContext.setTokenizeState(TokenizeStates.HEXADECIMAL_CHARACTER_REFERENCE_START_STATE);
        break;
      default:
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.DECIMAL_CHARACTER_REFERENCE_START_STATE);
        break;
    }
  }
  
}
