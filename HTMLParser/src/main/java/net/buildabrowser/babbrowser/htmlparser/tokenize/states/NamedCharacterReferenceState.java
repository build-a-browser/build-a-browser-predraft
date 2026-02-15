package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.MatchTrie;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class NamedCharacterReferenceState implements TokenizeState {

  private final Map<String, String> referenceMap;
  private final MatchTrie optionsWithoutAmpersandTrie;

  public NamedCharacterReferenceState(Map<String, String> referenceMap) {
    this.referenceMap = referenceMap;

    List<String> optionsWithoutAmpersand = new ArrayList<>();
    for (String option: referenceMap.keySet()) {
      optionsWithoutAmpersand.add(option.substring(1));
    }
    this.optionsWithoutAmpersandTrie = MatchTrie.compile(optionsWithoutAmpersand);
  }

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) throws IOException {
    // Automatically occurs upon no lookahead matched
    tokenizeContext.flushCodePointsConsumedAsACharacterReference(parseContext);
    // Since this stage technically does not consume unless a match is present,
    // but this method auto-consumes, reconsume.
    tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.ambiguousAmpersandState);
  }

  @Override
  public boolean lookaheadMatched(String value, TokenizeContext tokenizeContext, ParseContext parseContext) {
    String resolvedValue = referenceMap.get("&" + value);
    if (resolvedValue == null) return false;

    if (!value.endsWith(";")) {
      parseContext.parseError();
    }

    tokenizeContext.temporaryBuffer().clear();
    tokenizeContext.temporaryBuffer().append(resolvedValue);
    tokenizeContext.flushCodePointsConsumedAsACharacterReference(parseContext);
    tokenizeContext.setTokenizeState(tokenizeContext.getReturnState());

    return true;
  }

  @Override
  public MatchTrie lookaheadOptions() {
    return this.optionsWithoutAmpersandTrie;
  }
  
}
