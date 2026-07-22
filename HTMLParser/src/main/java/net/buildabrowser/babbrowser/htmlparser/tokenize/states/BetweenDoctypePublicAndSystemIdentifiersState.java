package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class BetweenDoctypePublicAndSystemIdentifiersState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    DoctypeToken doctypeToken = tokenizeContext.currentDoctypeToken();
    switch (ch) {
      case '\t', '\n', '\f', ' ':
        break;
      case '>':
        tokenizeContext.setTokenizeState(TokenizeStates.DATA_STATE);
        parseContext.emitDoctypeToken(doctypeToken);
        break;
      case '"':
        doctypeToken.setSystemIdentifier("");
        tokenizeContext.setTokenizeState(TokenizeStates.DOCTYPE_SYSTEM_IDENTIFIER_DOUBLE_QUOTED_STATE);
        break;
      case '\'':
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
