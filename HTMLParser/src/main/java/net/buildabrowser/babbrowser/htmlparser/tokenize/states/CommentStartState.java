package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class CommentStartState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    switch (ch) {
      case '-':
        tokenizeContext.setTokenizeState(TokenizeStates.commentStartDashState);
        break;
      case '>':
        // TODO: Parse-error
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
        break;
      default:
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.commentState);
        break;
    }
  }
  
}
