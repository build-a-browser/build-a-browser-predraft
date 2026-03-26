package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class DoctypeNameState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
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
        parseContext.parseError();
        tokenizeContext.currentDoctypeToken().appendCodePointToName('\uFFFD');
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
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
