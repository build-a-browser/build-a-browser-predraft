package net.buildabrowser.babbrowser.cssbase.property.table;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;

public class BorderSpacingParserTest {
  
  private final BorderSpacingParser borderSpacingParser = new BorderSpacingParser();
  
  @Test
  @DisplayName("Can parse single-component border-spacing value")
  public void canParseSingleComponentBorderSpacingValue() throws IOException {
    CSSValue value = borderSpacingParser.parse(
      CSSTokenStream.createForTesting(DimensionToken.create(1, "em")));
    Assertions.assertEquals(
      BorderSpacingValue.create(
        LengthValue.create(1, true, LengthType.EM),
        LengthValue.create(1, true, LengthType.EM)),
      value);
  }
  
  @Test
  @DisplayName("Can parse double-component border-spacing value")
  public void canParseDoubleComponentBorderSpacingValue() throws IOException {
    CSSValue value = borderSpacingParser.parse(
      CSSTokenStream.createForTesting(
        DimensionToken.create(2, "em"),
        DimensionToken.create(3, "em")));
    Assertions.assertEquals(
      BorderSpacingValue.create(
        LengthValue.create(2, true, LengthType.EM),
        LengthValue.create(3, true, LengthType.EM)),
      value);
  }

}
