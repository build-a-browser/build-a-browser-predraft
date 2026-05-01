package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class ScriptDataDoubleEscapeStartState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '\t', '\n', '\f', ' ', '/', '>':
        if (tokenizeContext.temporaryBuffer().get().equals("script")) {
          tokenizeContext.setTokenizeState(TokenizeStates.scriptDataDoubleEscapedState);
        } else {
          tokenizeContext.setTokenizeState(TokenizeStates.scriptDataEscapedState);
        }
        parseContext.emitCharacterToken(ch);
        return;
      default:
        break;
    }

    if (ASCIIUtil.isAlpha(ch)) {
      tokenizeContext.temporaryBuffer().append(ASCIIUtil.toLower(ch));
      parseContext.emitCharacterToken(ch);
    } else {
      tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.scriptDataEscapedState);
    }
  }
  
}
