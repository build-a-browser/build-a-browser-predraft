package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class ScriptDataDoubleEscapedLessThanSignState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    if (ch == '/') {
      tokenizeContext.temporaryBuffer().clear();
      tokenizeContext.setTokenizeState(TokenizeStates.scriptDataDoubleEscapeEndState);
      parseContext.emitCharacterToken('/');
    } else {
      tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.scriptDataDoubleEscapedState);
    }
  }
  
}
