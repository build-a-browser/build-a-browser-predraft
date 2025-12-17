package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class CommentLessThanSignBangDashDashState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    switch (ch) {
      case '>', TokenizeContext.EOF:
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.commentEndState);
        break;
      default:
        // TODO: Parse error
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.commentEndState);
        break;
    }
  }
  
}
