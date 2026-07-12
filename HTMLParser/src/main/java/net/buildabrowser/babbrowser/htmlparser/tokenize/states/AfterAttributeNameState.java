package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class AfterAttributeNameState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '\t', '\n', '\f', ' ':
        break;
      case '/':
        tokenizeContext.setTokenizeState(TokenizeStates.SELF_CLOSING_START_TAG_STATE);
        break;
      case '=':
        tokenizeContext.setTokenizeState(TokenizeStates.BEFORE_ATTRIBUTE_VALUE_STATE);
        parseContext.emitTagToken(tokenizeContext.currentTagToken());
        break;
      case '>':
        tokenizeContext.setTokenizeState(TokenizeStates.DATA_STATE);
        parseContext.emitTagToken(tokenizeContext.currentTagToken());
        break;
      case TokenizeContext.EOF:
        parseContext.parseError();
        parseContext.emitEOFToken();
        break;
      default:
        tokenizeContext.currentTagToken().startNewAttribute();
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.ATTRIBUTE_NAME_STATE);
    }
  }
  
}
