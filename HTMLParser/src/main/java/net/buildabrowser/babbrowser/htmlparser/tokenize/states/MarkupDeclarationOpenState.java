package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;
import java.util.List;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class MarkupDeclarationOpenState implements TokenizeState {

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    throw new UnsupportedOperationException("Not yet implemented!");
  }

  @Override
  public boolean lookaheadMatched(String value, TokenizeContext tokenizeContext, ParseContext parseContext) {
    // TODO: Implement the rest
    switch (value.toUpperCase()) {
      case "--":
        tokenizeContext.beginCommentToken();
        tokenizeContext.setTokenizeState(TokenizeStates.commentStartState);
        return true;
      case "DOCTYPE":
        tokenizeContext.setTokenizeState(TokenizeStates.doctypeState);
        return true;
      default:
        throw new UnsupportedOperationException("Not yet implemented!");
    }
  }

  @Override
  public List<String> lookaheadOptions() {
    return List.of(
      "--",
      "DOCTYPE",
      "[CDATA["
    );
  }

}
