package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;
import net.buildabrowser.babbrowser.htmlparser.tokenize.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.tokenize.util.TokenizeUtil;

public class RawTextEndTagNameState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    // TODO: Other cases
    switch (ch) {
      case '>':
        // TODO: Is it an appropriate token?
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
        parseContext.emitTagToken(tokenizeContext.currentTagToken());
        return;
      default:
        break;
    }

    if (ASCIIUtil.isAlpha(ch)) {
      tokenizeContext.currentTagToken().appendToName(ch);
      tokenizeContext.temporaryBuffer().append(ch);
      return;
    }
    
    parseContext.emitCharacterToken('<');
    parseContext.emitCharacterToken('/');
    TokenizeUtil.emitTemporaryBuffer(tokenizeContext, parseContext);
    tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.rawTextState);
  }

}
