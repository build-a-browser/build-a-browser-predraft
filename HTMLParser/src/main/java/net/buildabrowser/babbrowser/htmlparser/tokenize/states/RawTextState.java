package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class RawTextState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    // TODO: Other cases
    switch (ch) {
      case '<':
        tokenizeContext.setTokenizeState(TokenizeStates.rawTextLessThanSignState);
        break;
      default:
        parseContext.emitCharacterToken(ch);
    }
  }
  
}
