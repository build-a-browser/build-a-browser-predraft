package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class ScriptDataEscapedState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '-':
        tokenizeContext.setTokenizeState(TokenizeStates.SCRIPT_DATA_ESCAPED_DASH_STATE);
        parseContext.emitCharacterToken('-');
        break;
      case '<':
        tokenizeContext.setTokenizeState(TokenizeStates.SCRIPT_DATA_ESCAPED_LESS_THAN_SIGN_STATE);
        break;
      case 0:
        parseContext.parseError();
        parseContext.emitCharacterToken(0xFFFD);
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
        parseContext.emitEOFToken();
        break;
      default:
        parseContext.emitCharacterToken(ch);
        break;
    }
  }
  
}
