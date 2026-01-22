package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class ScriptDataEscapeStartDashState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    if (ch == '-') {
      tokenizeContext.setTokenizeState(TokenizeStates.scriptDataEscapedDashDashState);
      parseContext.emitCharacterToken('-');
    } else {
      tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.scriptDataState);
    }
  }
  
}
