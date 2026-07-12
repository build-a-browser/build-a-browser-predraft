package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class AfterDoctypePublicKeywordState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '\t', '\n', '\f', ' ':
        tokenizeContext.setTokenizeState(TokenizeStates.BEFORE_DOCTYPE_PUBLIC_IDENTIFIER_STATE);
        break;
      case '"':
        parseContext.parseError();
        tokenizeContext.currentDoctypeToken().setPublicIdentifier("");
        tokenizeContext.setTokenizeState(TokenizeStates.DOCTYPE_PUBLIC_IDENTIFIER_DOUBLE_QUOTED_STATE);
        break;
      case '\'':
        parseContext.parseError();
        tokenizeContext.currentDoctypeToken().setPublicIdentifier("");
        tokenizeContext.setTokenizeState(TokenizeStates.DOCTYPE_PUBLIC_IDENTIFIER_SINGLE_QUOTED_STATE);
        break;
      case '>':
        parseContext.parseError();
        tokenizeContext.currentDoctypeToken().setForceQuirks(true);
        tokenizeContext.setTokenizeState(TokenizeStates.DATA_STATE);
        parseContext.emitDoctypeToken(tokenizeContext.currentDoctypeToken());
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
        tokenizeContext.currentDoctypeToken().setForceQuirks(true);
        parseContext.emitDoctypeToken(tokenizeContext.currentDoctypeToken());
        parseContext.emitEOFToken();
        break;
      default:
        parseContext.parseError();
        tokenizeContext.currentDoctypeToken().setForceQuirks(true);
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.BOGUS_DOCTYPE_STATE);
        break;
    }
  }
  
}
