package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class CommentLessThanSignBangDashDashState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '>', TokenizeContext.EOF:
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.COMMENT_END_STATE);
        break;
      default:
        parseContext.parseError();
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.COMMENT_END_STATE);
        break;
    }
  }
  
}
