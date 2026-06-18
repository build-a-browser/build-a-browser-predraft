package net.buildabrowser.babbrowser.cssbase.property.outline;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.shared.LineStyleValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class OutlineStyleParserTest {
  
  private final OutlineStyleParser outlineStyleParser = new OutlineStyleParser();

  @Test
  @DisplayName("Can parse named outline style")
  public void canParseNamedOutlineStyle() throws IOException {
    CSSValue value = outlineStyleParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("solid")));
    Assertions.assertEquals(LineStyleValue.SOLID, value);
  }

    @Test
  @DisplayName("Can parse auto outline style")
  public void canParseAutoOutlineStyle() throws IOException {
    CSSValue value = outlineStyleParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("auto")));
    Assertions.assertEquals(CSSValue.AUTO, value);
  }

}
