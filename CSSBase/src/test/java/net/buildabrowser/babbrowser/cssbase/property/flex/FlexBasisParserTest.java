package net.buildabrowser.babbrowser.cssbase.property.flex;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;

public class FlexBasisParserTest {

  private final FlexBasisParser flexBasisParser = new FlexBasisParser();
  
  @Test
  @DisplayName("Can parse flex-basis value with content")
  public void canParseFlexBasisValueWithContent() throws IOException {
    CSSValue value = flexBasisParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("content")));
    Assertions.assertEquals(FlexBasisValue.CONTENT, value);
  }

  @Test
  @DisplayName("Can parse flex-basis value with size")
  public void canParseFlexBasisValueWithSize() throws IOException {
    CSSValue value = flexBasisParser.parse(
      CSSTokenStream.createForTesting(PercentageToken.create(5)));
    Assertions.assertEquals(PercentageValue.create(5), value);
  }

}
