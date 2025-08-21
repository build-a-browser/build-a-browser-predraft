package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;

public class TagOpenState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    switch (ch) {
      case '/':
        tokenizeContext.setTokenizeState(new EndTagOpenState());
        break;
      default:
        // TODO: Proper Alpha check, other cases
        tokenizeContext.beginTagToken(true);
        tokenizeContext.reconsumeInTokenizeState(ch, new TagNameState());
        break;
    }
  }

}
