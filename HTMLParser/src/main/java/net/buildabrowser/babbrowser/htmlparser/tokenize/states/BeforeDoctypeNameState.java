package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;
import net.buildabrowser.babbrowser.htmlparser.tokenize.util.ASCIIUtil;

public class BeforeDoctypeNameState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    if (ASCIIUtil.isAlpha(ch)) {
      tokenizeContext.beginDoctypeToken().appendCodePointToName(ASCIIUtil.toLower(ch));
      tokenizeContext.setTokenizeState(TokenizeStates.doctypeNameState);
      return;
    }

    switch (ch) {
      case '\t', '\n', '\f', ' ':
        break;
      case 0:
        // TODO: Parse error
        tokenizeContext.beginDoctypeToken().appendCodePointToName('\uFFFD');
        tokenizeContext.setTokenizeState(TokenizeStates.doctypeNameState);
        break;
      case '>':
        // TODO: Parse error
        DoctypeToken doctypeToken = tokenizeContext.beginDoctypeToken();
        doctypeToken.setForceQuirks(true);
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
        parseContext.emitDoctypeToken(doctypeToken);
        break;
      case TokenizeContext.EOF:
        // TODO: Parse error
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
