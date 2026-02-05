package net.buildabrowser.babbrowser.css.engine.property.border;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class BorderSizeParserTest {
  
  private final BorderSizeParser borderSizeParser = new BorderSizeParser(CSSProperty.BORDER_BOTTOM_WIDTH);

  @Test
  @DisplayName("Can parse border with named thickness")
  public void canParseBorderWithNamedThickness() throws IOException {
    CSSValue value = borderSizeParser.parse(
      CSSTokenStream.create(IdentToken.create("thin")));
    Assertions.assertEquals(LengthValue.create(2, true, LengthType.PX), value);
  }

  @Test
  @DisplayName("Can parse border with sized width")
  public void canParseBorderWithSizedWidth() throws IOException {
    CSSValue value = borderSizeParser.parse(
      CSSTokenStream.create(DimensionToken.create(5, "em")));
    Assertions.assertEquals(LengthValue.create(5, true, LengthType.EM), value);
  }

}
