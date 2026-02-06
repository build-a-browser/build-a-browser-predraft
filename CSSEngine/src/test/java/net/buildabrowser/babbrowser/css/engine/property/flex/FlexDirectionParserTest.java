package net.buildabrowser.babbrowser.css.engine.property.flex;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class FlexDirectionParserTest {
  
  private final FlexDirectionParser flexDirectionParser = new FlexDirectionParser();

  @Test
  @DisplayName("Can parse flex-direction value")
  public void canParseFlexDirectionValue() throws IOException {
    CSSValue value = flexDirectionParser.parse(
      CSSTokenStream.create(IdentToken.create("column-reverse")));
    Assertions.assertEquals(FlexDirectionValue.COLUMN_REVERSE, value);
  }

}
