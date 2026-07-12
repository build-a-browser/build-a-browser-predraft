package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class AfterDoctypePublicIdentifierState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    DoctypeToken doctypeToken = tokenizeContext.currentDoctypeToken();
    switch (ch) {
      case '\t', '\n', '\f', ' ':
        tokenizeContext.setTokenizeState(TokenizeStates.BETWEEN_DOCTYPE_PUBLIC_AND_SYSTEM_IDENTIFIERS_STATE);
        break;
      case '>':
        tokenizeContext.setTokenizeState(TokenizeStates.DATA_STATE);
        parseContext.emitDoctypeToken(doctypeToken);
        break;
      case '"':
        parseContext.parseError();
        doctypeToken.setSystemIdentifier("");
        tokenizeContext.setTokenizeState(TokenizeStates.DOCTYPE_SYSTEM_IDENTIFIER_DOUBLE_QUOTED_STATE);
        break;
      case '\'':
        parseContext.parseError();
        doctypeToken.setSystemIdentifier("");
        tokenizeContext.setTokenizeState(TokenizeStates.DOCTYPE_SYSTEM_IDENTIFIER_SINGLE_QUOTED_STATE);
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
        doctypeToken.setForceQuirks(true);
        parseContext.emitDoctypeToken(doctypeToken);
        parseContext.emitEOFToken();
        break;
      default:
        parseContext.parseError();
        doctypeToken.setForceQuirks(true);
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.BOGUS_DOCTYPE_STATE);
        break;
    }
  }
  
}
