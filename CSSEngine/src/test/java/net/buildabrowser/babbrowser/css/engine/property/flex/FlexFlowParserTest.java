package net.buildabrowser.babbrowser.css.engine.property.flex;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class FlexFlowParserTest {
  
  private final FlexFlowParser flexFlowParser = new FlexFlowParser();

  @Test
  @DisplayName("Can parse flex-flow value")
  public void canParseFlexFlowValue() throws IOException {
    CSSValue value = flexFlowParser.parse(
      CSSTokenStream.create(
        IdentToken.create("wrap"),
        IdentToken.create("column")));
    Assertions.assertEquals(
      FlexFlowValue.create(FlexDirectionValue.COLUMN, FlexWrapValue.WRAP),
      value);
  }

}
