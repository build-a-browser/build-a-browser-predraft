package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class ScriptDataEscapeStartDashState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    if (ch == '-') {
      tokenizeContext.setTokenizeState(TokenizeStates.SCRIPT_DATA_ESCAPED_DASH_DASH_STATE);
      parseContext.emitCharacterToken('-');
    } else {
      tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.SCRIPT_DATA_STATE);
    }
  }
  
}
