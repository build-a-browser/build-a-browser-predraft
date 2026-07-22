package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class AttributeValueDoubleQuotedState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      // TODO: Other cases
      case '"':
        tokenizeContext.setTokenizeState(TokenizeStates.AFTER_ATTRIBUTE_VALUE_QUOTED_STATE);
        break;
      case '&':
        tokenizeContext.setReturnState(this);
        tokenizeContext.setTokenizeState(TokenizeStates.CHARACTER_REFERENCE_STATE);
        break;
      case 0:
        parseContext.parseError();
        tokenizeContext.currentTagToken().appendToAttributeValue(0xFFFD);
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
        parseContext.emitEOFToken();
        break;
      default:
        tokenizeContext.currentTagToken().appendToAttributeValue(ch);
        break;
    }
  }

}
