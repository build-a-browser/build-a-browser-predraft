package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class TagOpenState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    switch (ch) {
      case '!':
        tokenizeContext.setTokenizeState(TokenizeStates.MARKUP_DECLARATION_OPEN_STATE);
        break;
      case '/':
        tokenizeContext.setTokenizeState(TokenizeStates.END_TAG_OPEN_STATE);
        break;
      default:
        // TODO: Proper Alpha check, other cases
        tokenizeContext.beginTagToken(true);
        tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.TAG_NAME_STATE);
        break;
    }
  }

}
