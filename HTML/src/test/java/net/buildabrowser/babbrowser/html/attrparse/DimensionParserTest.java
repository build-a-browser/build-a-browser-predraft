package net.buildabrowser.babbrowser.html.attrparse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.html.attrparse.DimensionParser.DimensionParserResult;

public class DimensionParserTest {
  
  @Test
  @DisplayName("Can parse integer-length dimension")
  public void canParseIntegerLengthDimension() {
    DimensionParserResult result = DimensionParser.parseDimension("6");
    Assertions.assertEquals(DimensionParserResult.length(6), result);
  }

  @Test
  @DisplayName("Can parse float-percentage dimension")
  public void canParseFloatPercentageDimension() {
    DimensionParserResult result = DimensionParser.parseDimension("6.5%");
    Assertions.assertEquals(DimensionParserResult.percentage(6.5f), result);
  }

  @Test
  @DisplayName("Can not parse invalid dimension")
  public void canNotParseInvalidDimension() {
    DimensionParserResult result = DimensionParser.parseDimension("abc");
    Assertions.assertNull(result);
  }

  @Test
  @DisplayName("Can not parse partially valid dimension")
  public void canParsePartiallyDimension() {
    DimensionParserResult result = DimensionParser.parseDimension("6.a5%");
    Assertions.assertEquals(DimensionParserResult.length(6), result);
  }

}
