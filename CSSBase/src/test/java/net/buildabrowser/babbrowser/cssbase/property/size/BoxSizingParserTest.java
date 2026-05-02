package net.buildabrowser.babbrowser.cssbase.property.size;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class BoxSizingParserTest {
  
  private static final BoxSizingParser boxSizingParser = new BoxSizingParser();

  @Test
  @DisplayName("Can parse box sizing value")
  public void canParseBoxSizingValue() throws IOException {
    CSSValue value = boxSizingParser.parse(
      CSSTokenStream.createForTesting(IdentToken.create("border-box")));
    Assertions.assertEquals(BoxSizingValue.BORDER_BOX, value);
  }

}
