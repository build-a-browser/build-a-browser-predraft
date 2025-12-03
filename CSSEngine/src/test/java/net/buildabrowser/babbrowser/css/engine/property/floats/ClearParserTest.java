package net.buildabrowser.babbrowser.css.engine.property.floats;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class ClearParserTest {

  private final ClearParser floatsParser = new ClearParser();
  
  @Test
  @DisplayName("Can parse clear value")
  public void canParseClearValue() throws IOException {
    CSSValue value = floatsParser.parse(
      CSSTokenStream.create(IdentToken.create("both")),
      ActiveStyles.create());
    Assertions.assertEquals(ClearValue.BOTH, value);
  }

}
