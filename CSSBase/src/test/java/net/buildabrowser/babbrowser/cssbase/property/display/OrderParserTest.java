package net.buildabrowser.babbrowser.cssbase.property.display;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;

public class OrderParserTest {
  
  private final OrderParser orderParser = new OrderParser();

  @Test
  @DisplayName("Can parse order value")
  public void canParseOrderValue() throws IOException {
    CSSValue value = orderParser.parse(
      CSSTokenStream.create(NumberToken.create(4)));
    Assertions.assertEquals(OrderValue.create(4), value);
  }

}
