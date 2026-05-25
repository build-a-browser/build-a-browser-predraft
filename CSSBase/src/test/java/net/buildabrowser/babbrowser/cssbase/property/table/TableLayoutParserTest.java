package net.buildabrowser.babbrowser.cssbase.property.table;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class TableLayoutParserTest {
  
  private final TableLayoutParser tableLayoutParser = new TableLayoutParser();
  
  @Test
  @DisplayName("Can parse border-collapse value")
  public void canParseBorderCollapseValue() throws IOException {
    CSSValue value = tableLayoutParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("fixed")));
    Assertions.assertEquals(TableLayoutValue.FIXED, value);
  }

}
