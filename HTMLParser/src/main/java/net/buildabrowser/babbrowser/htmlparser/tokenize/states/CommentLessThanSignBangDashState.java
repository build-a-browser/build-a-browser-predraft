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
        tokenizeContext.setTokenizeState(TokenizeStates.COMMENT_LESS_THAN_SIGN_BANG_DASH_DASH_STATE);
        break;
      default:
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.COMMENT_END_DASH_STATE);
        break;
    }
  }
  
}
