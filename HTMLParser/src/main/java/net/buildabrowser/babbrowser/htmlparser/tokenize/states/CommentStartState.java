package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class CommentStartState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '-':
        tokenizeContext.setTokenizeState(TokenizeStates.commentStartDashState);
        break;
      case '>':
        parseContext.parseError();
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
        break;
      default:
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.commentState);
        break;
    }
  }
  
}
