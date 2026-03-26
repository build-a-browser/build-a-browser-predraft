package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class BeforeDoctypeSystemIdentifierState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    DoctypeToken doctypeToken = tokenizeContext.currentDoctypeToken();
    switch (ch) {
      case '\t', '\n', '\f', ' ':
        break;
      case '"':
        doctypeToken.setSystemIdentifier("");
        tokenizeContext.setTokenizeState(TokenizeStates.doctypeSystemIdentifierDoubleQuotedState);
        break;
      case '\'':
        doctypeToken.setSystemIdentifier("");
        tokenizeContext.setTokenizeState(TokenizeStates.doctypeSystemIdentifierSingleQuotedState);
        break;
      case '>':
        parseContext.parseError();
        doctypeToken.setForceQuirks(true);
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
        parseContext.emitDoctypeToken(doctypeToken);
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
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.bogusDoctypeState);
        break;
    }
  }
  
}
