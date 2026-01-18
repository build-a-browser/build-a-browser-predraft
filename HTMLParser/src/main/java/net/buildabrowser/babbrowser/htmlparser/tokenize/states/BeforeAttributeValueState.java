package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class BeforeAttributeValueState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    switch (ch) {
      case '\t', '\n', '\f', ' ':
        break;
      case '"':
        tokenizeContext.setTokenizeState(TokenizeStates.attributeValueDoubleQuotedState);
        break;
      case '\'':
        tokenizeContext.setTokenizeState(TokenizeStates.attributeValueSingleQuotedState);
        break;
      case '>':
        parseContext.parseError();
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
        parseContext.emitTagToken(tokenizeContext.currentTagToken());
        break;
      default:
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.attributeValueUnquotedState);
        break;
    }
  }

}
