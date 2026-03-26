package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.common.util.ASCIIUtil;
import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class BeforeDoctypeNameState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    if (ASCIIUtil.isAlpha(ch)) {
      tokenizeContext.beginDoctypeToken().appendCodePointToName(ASCIIUtil.toLower(ch));
      tokenizeContext.setTokenizeState(TokenizeStates.doctypeNameState);
      return;
    }

    switch (ch) {
      case '\t', '\n', '\f', ' ':
        break;
      case 0:
        parseContext.parseError();
        tokenizeContext.beginDoctypeToken().appendCodePointToName('\uFFFD');
        tokenizeContext.setTokenizeState(TokenizeStates.doctypeNameState);
        break;
      case '>':
        parseContext.parseError();
        DoctypeToken doctypeToken = tokenizeContext.beginDoctypeToken();
        doctypeToken.setForceQuirks(true);
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
        parseContext.emitDoctypeToken(doctypeToken);
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
        doctypeToken = tokenizeContext.beginDoctypeToken();
        doctypeToken.setForceQuirks(true);
        parseContext.emitDoctypeToken(doctypeToken);
        parseContext.emitEOFToken();
        break;
      default:
        doctypeToken = tokenizeContext.beginDoctypeToken();
        doctypeToken.appendCodePointToName(ch);
        tokenizeContext.setTokenizeState(TokenizeStates.doctypeNameState);
        break;
    }
  }

}
