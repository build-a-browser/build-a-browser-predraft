package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class BeforeAttributeValueState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '\t', '\n', '\f', ' ':
        break;
      case '"':
        tokenizeContext.setTokenizeState(TokenizeStates.ATTRIBUTE_VALUE_DOUBLE_QUOTED_STATE);
        break;
      case '\'':
        tokenizeContext.setTokenizeState(TokenizeStates.ATTRIBUTE_VALUE_SINGLE_QUOTED_STATE);
        break;
      case '>':
        parseContext.parseError();
        tokenizeContext.setTokenizeState(TokenizeStates.DATA_STATE);
        parseContext.emitTagToken(tokenizeContext.currentTagToken());
        break;
      default:
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.ATTRIBUTE_VALUE_UNQUOTED_STATE);
        break;
    }
  }

}
