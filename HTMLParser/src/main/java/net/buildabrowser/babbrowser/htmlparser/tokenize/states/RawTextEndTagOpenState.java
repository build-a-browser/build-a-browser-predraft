package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;
import net.buildabrowser.babbrowser.htmlparser.tokenize.util.ASCIIUtil;

public class RawTextEndTagOpenState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    if (ASCIIUtil.isAlpha(ch)) {
      tokenizeContext.beginTagToken(false);
      tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.rawTextEndTagNameState);
    } else {
      parseContext.emitCharacterToken('<');
      parseContext.emitCharacterToken('/');
    }
  }

}
