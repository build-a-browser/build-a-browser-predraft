package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class DoctypeSystemIdentifierSingleQuotedState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    DoctypeToken doctypeToken = tokenizeContext.currentDoctypeToken();
    switch (ch) {
      case '\'':
        tokenizeContext.setTokenizeState(TokenizeStates.AFTER_DOCTYPE_SYSTEM_IDENTIFIER_STATE);
        break;
      case 0:
        parseContext.parseError();
        doctypeToken.appendCodePointToSystemIdentifier(0xFFFD);
        break;
      case '>':
        parseContext.parseError();
        doctypeToken.setForceQuirks(true);
        tokenizeContext.setTokenizeState(TokenizeStates.DATA_STATE);
        parseContext.emitDoctypeToken(doctypeToken);
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
        doctypeToken.setForceQuirks(true);
        parseContext.emitDoctypeToken(doctypeToken);
        parseContext.emitEOFToken();
        break;
      default:
        doctypeToken.appendCodePointToSystemIdentifier(ch);
        break;
    }
  }
  
}
