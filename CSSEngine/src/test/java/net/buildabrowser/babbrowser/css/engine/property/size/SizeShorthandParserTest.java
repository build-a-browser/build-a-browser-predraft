package net.buildabrowser.babbrowser.css.engine.property.size;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.shared.ManySideValue;
import net.buildabrowser.babbrowser.css.engine.property.shared.ManySideShorthandParser;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;

public class SizeShorthandParserTest {

  private static final ManySideShorthandParser sizeShorthandParser =
    new ManySideShorthandParser(new SizeParser(false, false, null)::parseInternal,
      new CSSProperty[] { CSSProperty.PADDING_TOP, CSSProperty.PADDING_RIGHT, CSSProperty.PADDING_BOTTOM, CSSProperty.PADDING_LEFT },
      CSSProperty.PADDING);
  
  @Test
  @DisplayName("Can parse shorthand manysize value")
  public void canParseShorthandManySize() throws IOException {
    CSSValue value = sizeShorthandParser.parse(
      CSSTokenStream.create(
        DimensionToken.create(4, "em"),
        DimensionToken.create(2, "em"),
      DimensionToken.create(1, "em")),
      ActiveStyles.create());
    Assertions.assertEquals(
      new ManySideValue(
        LengthValue.create(4, true, LengthType.EM),
        LengthValue.create(2, true, LengthType.EM),
        LengthValue.create(1, true, LengthType.EM),
        LengthValue.create(2, true, LengthType.EM)
      ),
      value);
  }

}
