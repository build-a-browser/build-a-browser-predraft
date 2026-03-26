package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class AmbiguousAmpersandState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    if (ch ==  ';') {
      parseContext.parseError();
      tokenizeContext.reconsumeInTokenizeState(ch, tokenizeContext.getReturnState());
    } else if (ASCIIUtil.isAlpha(ch)) {
      if (tokenizeContext.getReturnState().equals(TokenizeStates.dataState)) {
        parseContext.emitCharacterToken(ch);
      } else {
        tokenizeContext.currentTagToken().appendToAttributeValue(ch);
      }
    } else {
      tokenizeContext.reconsumeInTokenizeState(ch, tokenizeContext.getReturnState());
    }
  }
  
}
