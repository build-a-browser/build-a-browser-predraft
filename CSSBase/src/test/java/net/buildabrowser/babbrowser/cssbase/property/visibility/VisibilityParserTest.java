package net.buildabrowser.babbrowser.cssbase.property.visibility;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class VisibilityParserTest {

  private final VisibilityParser visibilityParser = new VisibilityParser();
  
  @Test
  @DisplayName("Can parse visibility value")
  public void canParseVisibilityValue() throws IOException {
    CSSValue value = visibilityParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("hidden")));
    Assertions.assertEquals(VisibilityValue.HIDDEN, value);
  }

}
