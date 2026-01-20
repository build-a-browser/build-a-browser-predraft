package net.buildabrowser.babbrowser.css.engine.property.border;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorBaseParser;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;

public class BorderColorParser implements PropertyValueParser {
  
private static final CSSFailure EXPECTED_EOF = new CSSFailure("Expected an EOF token");

  private final PropertyValueParser innerParser;
  private final CSSProperty relatedProperty;

  public BorderColorParser(CSSProperty property) {
    this.innerParser = new ColorBaseParser();
    this.relatedProperty = property;
  }

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    CSSValue result = innerParser.parse(stream, activeStyles);
    if (result.isFailure()) return result;
    if (!(stream.peek() instanceof EOFToken)) {
      return EXPECTED_EOF;
    }

    activeStyles.setProperty(innerParser.relatedProperty(), result);
    return result;
  }

  @Override
  public CSSProperty relatedProperty() {
    return relatedProperty;
  }

}
