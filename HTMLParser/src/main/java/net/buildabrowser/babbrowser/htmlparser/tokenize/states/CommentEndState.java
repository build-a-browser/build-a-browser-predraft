package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class CommentEndState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '>':
        tokenizeContext.setTokenizeState(TokenizeStates.DATA_STATE);
        parseContext.emitCommentToken(tokenizeContext.currentCommentToken());
        break;
      case '!':
        tokenizeContext.setTokenizeState(TokenizeStates.COMMENT_END_BANG_STATE);
        break;
      case '-':
        tokenizeContext.currentCommentToken().appendCodePointToData('-');
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
        parseContext.emitCommentToken(tokenizeContext.currentCommentToken());
        parseContext.emitEOFToken();
        break;
      default:
        tokenizeContext.currentCommentToken().appendCodePointToData('-');
        tokenizeContext.currentCommentToken().appendCodePointToData('-');
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.COMMENT_STATE);
    }
  }
  
}
