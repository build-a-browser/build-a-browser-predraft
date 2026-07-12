package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class AttributeValueUnquotedState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '\t', '\n', '\f', ' ':
        tokenizeContext.setTokenizeState(TokenizeStates.BEFORE_ATTRIBUTE_NAME_STATE);
        break;
      case '&':
        tokenizeContext.setReturnState(this);
        tokenizeContext.setTokenizeState(TokenizeStates.CHARACTER_REFERENCE_STATE);
        break;
      case '>':
        tokenizeContext.setTokenizeState(TokenizeStates.DATA_STATE);
        parseContext.emitTagToken(tokenizeContext.currentTagToken());
        break;
      case 0:
        parseContext.parseError();
        tokenizeContext.currentTagToken().appendToAttributeValue(0xFFFD);
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
        parseContext.emitEOFToken();
        break;
      case '"', '\'', '<', '=', '`':
        parseContext.parseError();
        // Fall-through
      default:
        tokenizeContext.currentTagToken().appendToAttributeValue(ch);
        break;
    }
  }
  
}
