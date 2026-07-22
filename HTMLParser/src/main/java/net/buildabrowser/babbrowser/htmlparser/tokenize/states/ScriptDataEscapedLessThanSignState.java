package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class ScriptDataEscapedLessThanSignState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    if (ch == '/') {
      tokenizeContext.temporaryBuffer().clear();
      tokenizeContext.setTokenizeState(TokenizeStates.SCRIPT_DATA_ESCAPED_END_TAG_OPEN_STATE);
    } else if (ASCIIUtil.isAlpha(ch)) {
      tokenizeContext.temporaryBuffer().clear();
      parseContext.emitCharacterToken('<');
      tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.SCRIPT_DATA_DOUBLE_ESCAPE_START_STATE);
    } else {
      parseContext.emitCharacterToken('<');
      tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.SCRIPT_DATA_ESCAPED_STATE);
    }
  }
  
}
