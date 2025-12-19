package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;
import net.buildabrowser.babbrowser.htmlparser.tokenize.util.ASCIIUtil;

public class DoctypeNameState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    if (ASCIIUtil.isAlpha(ch)) {
      tokenizeContext.currentDoctypeToken().appendCodePointToName(ASCIIUtil.toLower(ch));
      return;
    }

    switch (ch) {
      case '\t', '\n', '\f', ' ':
        tokenizeContext.setTokenizeState(TokenizeStates.afterDoctypeNameState);
        break;
      case '>':
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
        parseContext.emitDoctypeToken(tokenizeContext.currentDoctypeToken());
        break;
      case 0:
        // TODO: Parse error
        tokenizeContext.currentDoctypeToken().appendCodePointToName('\uFFFD');
        break;
      case TokenizeContext.EOF:
        // TODO: Parse error
        DoctypeToken doctypeToken = tokenizeContext.currentDoctypeToken();
        doctypeToken.setForceQuirks(true);
        parseContext.emitDoctypeToken(doctypeToken);
        parseContext.emitEOFToken();
        break;
      default:
        tokenizeContext.currentDoctypeToken().appendCodePointToName(ch);
        break;
    }
  }

}
