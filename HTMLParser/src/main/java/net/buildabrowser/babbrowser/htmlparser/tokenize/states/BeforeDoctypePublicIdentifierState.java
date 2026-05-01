package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class BeforeDoctypePublicIdentifierState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '\t', '\n', '\f', ' ':
        break;
      case '"':
        tokenizeContext.currentDoctypeToken().setPublicIdentifier("");
        tokenizeContext.setTokenizeState(TokenizeStates.doctypePublicIdentifierDoubleQuotedState);
        break;
      case '\'':
        tokenizeContext.currentDoctypeToken().setPublicIdentifier("");
        tokenizeContext.setTokenizeState(TokenizeStates.doctypePublicIdentifierSingleQuotedState);
        break;
      case '>':
        parseContext.parseError();
        tokenizeContext.currentDoctypeToken().setForceQuirks(true);
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
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
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.bogusDoctypeState);
        break;
    }
  }
  
}
