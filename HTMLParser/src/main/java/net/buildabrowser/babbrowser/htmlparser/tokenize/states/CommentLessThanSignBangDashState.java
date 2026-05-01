package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class CommentLessThanSignBangDashState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '-':
        tokenizeContext.setTokenizeState(TokenizeStates.commentLessThanSignBangDashDashState);
        break;
      default:
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.commentEndDashState);
        break;
    }
  }
  
}
