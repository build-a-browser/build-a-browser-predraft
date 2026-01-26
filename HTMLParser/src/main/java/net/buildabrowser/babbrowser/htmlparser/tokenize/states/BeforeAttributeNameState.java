package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class BeforeAttributeNameState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    switch (ch) {
      case '\t', '\n', '\f', ' ':
        break;
      case '/', '>', TokenizeContext.EOF:
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.afterAttributeNameState);
        break;
      case '=':
        tokenizeContext.setTokenizeState(TokenizeStates.beforeAttributeValueState);
        break;
      default:
        tokenizeContext.currentTagToken().startNewAttribute();
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.attributeNameState);
        break;
    }
  }

}
