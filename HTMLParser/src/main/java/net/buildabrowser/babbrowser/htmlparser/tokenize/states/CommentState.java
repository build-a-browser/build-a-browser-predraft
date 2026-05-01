package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class CommentState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '<':
        tokenizeContext.currentCommentToken().appendCodePointToData(ch);
        tokenizeContext.setTokenizeState(TokenizeStates.commentLessThanSignState);
        break;
      case '-':
        tokenizeContext.setTokenizeState(TokenizeStates.commentEndDashState);
        break;
      case 0:
        parseContext.parseError();
        tokenizeContext.currentCommentToken().appendCodePointToData(0xFFFFD);
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
        parseContext.emitCommentToken(tokenizeContext.currentCommentToken());
        parseContext.emitEOFToken();
        break;
      default:
        tokenizeContext.currentCommentToken().appendCodePointToData(ch);
        break;
    }
  }
  
}
