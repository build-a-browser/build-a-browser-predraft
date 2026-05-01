package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class DoctypeState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '\t', '\n', '\f', ' ':
        tokenizeContext.setTokenizeState(TokenizeStates.beforeDoctypeNameState);
        break;
      case '>':
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.beforeDoctypeNameState);
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
        DoctypeToken doctypeToken = DoctypeToken.create();
        doctypeToken.setForceQuirks(true);
        parseContext.emitDoctypeToken(doctypeToken);
        parseContext.emitEOFToken();
        break;
      default:
        parseContext.parseError();
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.beforeDoctypeNameState);
    }
  }

}
