package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class CommentState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    switch (ch) {
      case '<':
        tokenizeContext.currentCommentToken().appendCodePointToData(ch);
        tokenizeContext.setTokenizeState(TokenizeStates.commentLessThanSignState);
        break;
      case '-':
        tokenizeContext.setTokenizeState(TokenizeStates.commentEndDashState);
        break;
      case 0:
        // TODO: Parse error
        tokenizeContext.currentCommentToken().appendCodePointToData(0xFFFFD);
        break;
      case TokenizeContext.EOF:
        // TODO: Parse error
        parseContext.emitCommentToken(tokenizeContext.currentCommentToken());
        parseContext.emitEOFToken();
        break;
      default:
        tokenizeContext.currentCommentToken().appendCodePointToData(ch);
        break;
    }
  }
  
}
