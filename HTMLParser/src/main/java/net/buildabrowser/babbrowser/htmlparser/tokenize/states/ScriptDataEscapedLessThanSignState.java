package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;
import net.buildabrowser.babbrowser.htmlparser.tokenize.util.ASCIIUtil;

public class ScriptDataEscapedLessThanSignState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    if (ch == '/') {
      tokenizeContext.temporaryBuffer().clear();
      tokenizeContext.setTokenizeState(TokenizeStates.scriptDataEscapedEndTagOpenState);
    } else if (ASCIIUtil.isAlpha(ch)) {
      tokenizeContext.temporaryBuffer().clear();
      parseContext.emitCharacterToken('<');
      tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.scriptDataDoubleEscapeStartState);
    } else {
      parseContext.emitCharacterToken('<');
      tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.scriptDataEscapedState);
    }
  }
  
}
