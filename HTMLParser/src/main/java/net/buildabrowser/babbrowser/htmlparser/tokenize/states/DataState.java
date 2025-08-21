package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;

public class DataState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext context, ParseContext parseContext) {
    switch (ch) {
      // TODO: Other cases
      case TokenizeContext.EOF:
        parseContext.emitEOFToken();
        break;
      case '<':
        context.setTokenizeState(new TagOpenState());
        break;
      default:
        parseContext.emitCharacterToken(ch);
        break;
    }
  }
  
}
