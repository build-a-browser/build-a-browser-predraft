package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class CommentLessThanSignState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    switch (ch) {
      case '!':
        tokenizeContext.currentCommentToken().appendCodePointToData(ch);
        tokenizeContext.setTokenizeState(TokenizeStates.commentLessThanSignBangState);
        break;
      case '<':
        tokenizeContext.currentCommentToken().appendCodePointToData(ch);
        break;
      default:
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.commentState);
        break;
    }
  }
  
}
