package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class DecimalCharacterReferenceState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    if (ASCIIUtil.isDigit(ch)) {
      tokenizeContext.setCharacterReferenceCode(tokenizeContext.getCharacterReferenceCode() * 10 + ch - '0');
    } else if (ch == ';') {
      tokenizeContext.setTokenizeState(TokenizeStates.numericCharacterReferenceEndState);
    } else {
      parseContext.parseError();
      tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.numericCharacterReferenceEndState);
    }
  }
  
}
