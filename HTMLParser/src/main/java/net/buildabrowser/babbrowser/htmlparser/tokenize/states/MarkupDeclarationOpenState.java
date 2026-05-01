package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.util.List;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.MatchTrie;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class MarkupDeclarationOpenState implements TokenizeState {

  private static final MatchTrie OPTIONS_TRIE = MatchTrie.compile(
    List.of("--", "DOCTYPE", "[CDATA["));

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
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
  public MatchTrie lookaheadOptions() {
    return OPTIONS_TRIE;
  }

}
