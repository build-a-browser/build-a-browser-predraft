package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class ScriptDataDoubleEscapedState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    switch (ch) {
      case '-':
        tokenizeContext.setTokenizeState(TokenizeStates.scriptDataDoubleEscapedDashState);
        parseContext.emitCharacterToken('-');
        break;
      case '<':
        tokenizeContext.setTokenizeState(TokenizeStates.scriptDataDoubleEscapedLessThanSignState);
        parseContext.emitCharacterToken('<');
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
