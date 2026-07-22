package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;
import net.buildabrowser.babbrowser.htmlparser.tokenize.util.TokenizeUtil;

public class RCDataEndTagNameState implements TokenizeState {

    @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    if (parseContext.isAppropriateEndTagToken(tokenizeContext.currentTagToken())) {
      switch (ch) {
        case '\t', '\n', '\f', ' ':
          tokenizeContext.setTokenizeState(TokenizeStates.BEFORE_ATTRIBUTE_NAME_STATE);
          return;
        case '/':
          tokenizeContext.setTokenizeState(TokenizeStates.SELF_CLOSING_START_TAG_STATE);
          return;
        case '>':
          tokenizeContext.setTokenizeState(TokenizeStates.DATA_STATE);
          parseContext.emitTagToken(tokenizeContext.currentTagToken());
          return;
        default:
          break;
      }
    }

    if (ASCIIUtil.isAlpha(ch)) {
      tokenizeContext.currentTagToken().appendToName(ch);
      tokenizeContext.temporaryBuffer().append(ch);
      return;
    }
    
    parseContext.emitCharacterToken('<');
    parseContext.emitCharacterToken('/');
    TokenizeUtil.emitTemporaryBuffer(tokenizeContext, parseContext);
    tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.RCDATA_STATE);
  }

}
