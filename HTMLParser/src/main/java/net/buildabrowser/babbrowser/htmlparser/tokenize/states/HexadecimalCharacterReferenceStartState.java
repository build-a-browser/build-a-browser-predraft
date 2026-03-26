package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class HexadecimalCharacterReferenceStartState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    if (ASCIIUtil.isHexDigit(ch)) {
      tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.hexadecimalCharacterReferenceState);
    } else {
      parseContext.parseError();
      tokenizeContext.flushCodePointsConsumedAsACharacterReference(parseContext);
      tokenizeContext.reconsumeInTokenizeState(ch, tokenizeContext.getReturnState());
    }
  }
  
  

}
