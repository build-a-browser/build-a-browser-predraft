package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.util.List;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.token.DoctypeToken;
import net.buildabrowser.babbrowser.htmlparser.tokenize.MatchTrie;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class AfterDoctypeNameState implements TokenizeState {

  private static final MatchTrie OPTIONS_TRIE = MatchTrie.compile(List.of("PUBLIC", "SYSTEM"));

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '\t', '\n', '\f', ' ':
        break;
      case '>':
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
        parseContext.emitDoctypeToken(tokenizeContext.currentDoctypeToken());
      case TokenizeContext.EOF:
        parseContext.parseError();
        DoctypeToken doctypeToken = tokenizeContext.currentDoctypeToken();
        doctypeToken.setForceQuirks(true);
        parseContext.emitDoctypeToken(doctypeToken);
        parseContext.emitEOFToken();
        break;
      default:
        parseContext.parseError();
        tokenizeContext.currentDoctypeToken().setForceQuirks(true);
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.bogusDoctypeState);
        break;
    }
  }

  @Override
  public boolean lookaheadMatched(String value, TokenizeContext tokenizeContext, ParseContext parseContext) {
    if (value.toUpperCase().equals("PUBLIC")) {
      tokenizeContext.setTokenizeState(TokenizeStates.afterDoctypePublicKeywordState);
      return true;
    } else if (value.toUpperCase().equals("SYSTEM")) {
      tokenizeContext.setTokenizeState(TokenizeStates.afterDoctypeSystemKeywordState);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public MatchTrie lookaheadOptions() {
    return OPTIONS_TRIE;
  }

}
