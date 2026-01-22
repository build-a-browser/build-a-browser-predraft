package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class ScriptDataEscapedDashState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    switch (ch) {
      case '-':
        tokenizeContext.setTokenizeState(TokenizeStates.scriptDataEscapedDashDashState);
        parseContext.emitCharacterToken('-');
        break;
      case '<':
        tokenizeContext.setTokenizeState(TokenizeStates.scriptDataEscapedLessThanSignState);
        break;
      case 0:
        parseContext.parseError();
        tokenizeContext.setTokenizeState(TokenizeStates.scriptDataEscapedState);
        parseContext.emitCharacterToken(0xFFFD);
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
        parseContext.emitEOFToken();
        break;
      default:
        tokenizeContext.setTokenizeState(TokenizeStates.scriptDataEscapedState);
        parseContext.emitCharacterToken(ch);
        break;
    }
  }
  
}
