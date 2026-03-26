package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class ScriptDataEscapedEndTagOpenState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    if (ASCIIUtil.isAlpha(ch)) {
      tokenizeContext.beginTagToken(false);
      tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.scriptDataEscapedEndTagNameState);
    } else {
      parseContext.emitCharacterToken('<');
      parseContext.emitCharacterToken('/');
      tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.scriptDataEscapedState);
    }
  }
  
}
