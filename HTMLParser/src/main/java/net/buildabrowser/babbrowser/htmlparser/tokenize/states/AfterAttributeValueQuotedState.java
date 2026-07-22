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
        tokenizeContext.setTokenizeState(TokenizeStates.BEFORE_ATTRIBUTE_NAME_STATE);
        break;
      case '/':
        tokenizeContext.setTokenizeState(TokenizeStates.SELF_CLOSING_START_TAG_STATE);
        break;
      case '>':
        tokenizeContext.setTokenizeState(TokenizeStates.DATA_STATE);
        parseContext.emitTagToken(tokenizeContext.currentTagToken());
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
        break;
      default:
        parseContext.parseError();
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.BEFORE_ATTRIBUTE_NAME_STATE);
    }
  }

}
