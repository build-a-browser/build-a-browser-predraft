package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class CommentEndState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    switch (ch) {
      case '>':
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
        parseContext.emitCommentToken(tokenizeContext.currentCommentToken());
        break;
      case '!':
        tokenizeContext.setTokenizeState(TokenizeStates.commentEndBangState);
        break;
      case '-':
        tokenizeContext.currentCommentToken().appendCodePointToData('-');
        break;
      case TokenizeContext.EOF:
        // TODO: Parse-error
        parseContext.emitCommentToken(tokenizeContext.currentCommentToken());
        parseContext.emitEOFToken();
        break;
      default:
        tokenizeContext.currentCommentToken().appendCodePointToData('-');
        tokenizeContext.currentCommentToken().appendCodePointToData('-');
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.commentState);
    }
  }
  
}
