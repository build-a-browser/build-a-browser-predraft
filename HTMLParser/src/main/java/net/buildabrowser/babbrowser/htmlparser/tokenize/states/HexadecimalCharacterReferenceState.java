package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class HexadecimalCharacterReferenceState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    if (ASCIIUtil.isHexDigit(ch)) {
      tokenizeContext.setCharacterReferenceCode(tokenizeContext.getCharacterReferenceCode() * 16 + ASCIIUtil.hexValue(ch));
    } else if (ch == ';') {
      tokenizeContext.setTokenizeState(TokenizeStates.NUMERIC_CHARACTER_REFERENCE_END_STATE);
    } else {
      parseContext.parseError();
      tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.NUMERIC_CHARACTER_REFERENCE_END_STATE);
    }
  }
  
}
