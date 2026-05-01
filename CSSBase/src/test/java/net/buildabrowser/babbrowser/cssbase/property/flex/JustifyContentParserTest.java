package net.buildabrowser.babbrowser.cssbase.property.flex;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class JustifyContentParserTest {
  
  private final JustifyContentParser justifyContentParser = new JustifyContentParser();

  @Test
  @DisplayName("Can parse justify-content value")
  public void canParseJustifyContentValue() throws IOException {
    CSSValue value = justifyContentParser.parse(
      CSSTokenStream.create(IdentToken.create("space-between")));
    Assertions.assertEquals(JustifyContentValue.SPACE_BETWEEN, value);
  }

}
