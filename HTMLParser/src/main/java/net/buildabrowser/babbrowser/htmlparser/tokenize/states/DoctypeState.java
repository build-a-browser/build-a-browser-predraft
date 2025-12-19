package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class DoctypeState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    switch (ch) {
      case '\t', '\n', '\f', ' ':
        tokenizeContext.setTokenizeState(TokenizeStates.beforeDoctypeNameState);
        break;
      case '>':
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.beforeDoctypeNameState);
        break;
      case TokenizeContext.EOF:
        // TODO: Parse error
        DoctypeToken doctypeToken = DoctypeToken.create();
        doctypeToken.setForceQuirks(true);
        parseContext.emitDoctypeToken(doctypeToken);
        parseContext.emitEOFToken();
        break;
      default:
        // TODO: Parse error
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.beforeDoctypeNameState);
    }
  }

}
