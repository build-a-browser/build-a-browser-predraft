package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class AttributeValueUnquotedState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    switch (ch) {
      case '\t', '\n', '\f', ' ':
        tokenizeContext.setTokenizeState(TokenizeStates.beforeAttributeNameState);
        break;
      // TODO: Character reference
      case '>':
        tokenizeContext.setTokenizeState(TokenizeStates.dataState);
        parseContext.emitTagToken(tokenizeContext.currentTagToken());
        break;
      case 0:
        // TODO: Parse Error
        tokenizeContext.currentTagToken().appendToAttributeValue(0xFFFD);
        break;
      case TokenizeContext.EOF:
        // TODO: Parse Error
        parseContext.emitEOFToken();
        break;
      case '"', '\'', '<', '=', '`':
        // TODO: Parse Error
        // Fall-through
      default:
        tokenizeContext.currentTagToken().appendToAttributeValue(ch);
        break;
    }
  }
  
}
