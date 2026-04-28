package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class DataState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext context, ParseContext parseContext) {
    switch (ch) {
      case '&':
        emitDataBuffer(context, parseContext);
        context.setReturnState(this);
        context.setTokenizeState(TokenizeStates.characterReferenceState);
        break;
      case '<':
        emitDataBuffer(context, parseContext);
        context.setTokenizeState(TokenizeStates.tagOpenState);
        break;
      case 0:
        emitDataBuffer(context, parseContext);
        parseContext.parseError();
        parseContext.emitCharacterToken(ch);
        break;
      case TokenizeContext.EOF:
        emitDataBuffer(context, parseContext);
        parseContext.emitEOFToken();
        break;
      default:
        switch (ch) {
          case '\t', '\n', '\f', '\r', ' ' -> {
            emitDataBuffer(context, parseContext);
            parseContext.emitCharacterToken(ch);
          }
          default -> context.dataBuffer().append(ch);
        }
        
        break;
    }
  }

  // Non-spec optimization to avoid repeat character processing
  private void emitDataBuffer(
    TokenizeContext context, ParseContext parseContext
  ) {
    String data = context.dataBuffer().get();
    context.dataBuffer().clear();
    if (data.isEmpty()) return;
    parseContext.emitOptimizedString(data);
  }
  
}
