package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class AfterAttributeValueQuotedState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      // TODO: Other cases
      case '\t', '\n', '\f', ' ':
        tokenizeContext.setTokenizeState(TokenizeStates.beforeAttributeNameState);
        break;
      case '/':
        tokenizeContext.setTokenizeState(TokenizeStates.selfClosingStartTagState);
        break;
      case '>':
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
        parseContext.emitTagToken(tokenizeContext.currentTagToken());
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
        break;
      default:
        parseContext.parseError();
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.beforeAttributeNameState);
    }
  }

}
