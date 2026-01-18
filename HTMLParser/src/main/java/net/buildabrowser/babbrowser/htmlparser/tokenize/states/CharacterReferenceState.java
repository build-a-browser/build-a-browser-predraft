package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext.TemporaryBuffer;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;
import net.buildabrowser.babbrowser.htmlparser.tokenize.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;

public class CharacterReferenceState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    TemporaryBuffer temporaryBuffer = tokenizeContext.temporaryBuffer();
    temporaryBuffer.clear();
    temporaryBuffer.append('&');
    if (ch == '#') {
      temporaryBuffer.append(ch);
      tokenizeContext.setTokenizeState(TokenizeStates.numericCharacterReferenceState);
    } else if (ASCIIUtil.isAlpha(ch)) {
      tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.namedCharacterReferenceState);
    } else {
      tokenizeContext.flushCodePointsConsumedAsACharacterReference(parseContext);
      tokenizeContext.reconsumeInTokenizeState(ch, tokenizeContext.getReturnState());
    }
  }
  
}
