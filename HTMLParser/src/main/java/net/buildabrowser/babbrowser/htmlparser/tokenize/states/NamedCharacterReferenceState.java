package net.buildabrowser.babbrowser.htmlparser.tokenize.states;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.buildabrowser.babbrowser.htmlparser.shared.ParseContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.MatchTrie;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeContext;
import net.buildabrowser.babbrowser.htmlparser.tokenize.TokenizeState;
import net.buildabrowser.babbrowser.htmlparser.tokenize.imp.TokenizeStates;

public class NamedCharacterReferenceState implements TokenizeState {

  private static final Logger LOGGER = LoggerFactory.getLogger(NamedCharacterReferenceState.class);

  private static Map<String, String> REFERENCE_MAP;

  private final MatchTrie optionsWithoutAmpersandTrie;

  public NamedCharacterReferenceState() {
    if (REFERENCE_MAP == null) {
      LOGGER.warn(
        "REFERENCE_MAP should be loaded via HTMLParser#initialize.\n"
        + "Attempting to initialize it late via ClassLoader.");
      initialize(ClassLoader.getSystemClassLoader()::getResourceAsStream);
    }

    List<String> optionsWithoutAmpersand = new ArrayList<>();
    for (String option: REFERENCE_MAP.keySet()) {
      optionsWithoutAmpersand.add(option.substring(1));
    }
    this.optionsWithoutAmpersandTrie = MatchTrie.compile(optionsWithoutAmpersand);
  }

  @Override
  public void consume(int ch, TokenizeContext tokenizeContext, ParseContext parseContext) {
    // Automatically occurs upon no lookahead matched
    tokenizeContext.flushCodePointsConsumedAsACharacterReference(parseContext);
    // Since this stage technically does not consume unless a match is present,
    // but this method auto-consumes, reconsume.
    tokenizeContext.reconsumeInTokenizeState(ch, TokenizeStates.ambiguousAmpersandState);
  }

  @Override
  public boolean lookaheadMatched(String value, TokenizeContext tokenizeContext, ParseContext parseContext) {
    String resolvedValue = REFERENCE_MAP.get("&" + value);
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

  public static void initialize(Function<String, InputStream> resourceLoader) {
    JsonObject refObj = JsonParser.parseReader(new InputStreamReader(
      resourceLoader.apply("ua/charrefs.json")))
      .getAsJsonObject();
    Map<String, String> refMap = new HashMap<>();
    for (Entry<String, JsonElement> entry: refObj.entrySet()) {
      if (entry.getKey().startsWith("_")) continue;
      refMap.put(
        entry.getKey(),
        entry.getValue().getAsJsonObject()
          .get("characters").getAsString());
    }

    REFERENCE_MAP = refMap;
  }
  
}
