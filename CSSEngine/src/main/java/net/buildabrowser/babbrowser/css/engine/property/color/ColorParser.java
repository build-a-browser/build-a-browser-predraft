package net.buildabrowser.babbrowser.css.engine.property.color;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;

public class ColorParser implements PropertyValueParser {

  private final PropertyValueParser colorBaseParser = new ColorBaseParser();

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    CSSValue value = colorBaseParser.parse(stream, null);

    if (!(stream.peek() instanceof EOFToken)) return CSSFailure.EXPECTED_EOF;
    if (value instanceof ColorValue colorValue) {
      activeStyles.setProperty(CSSProperty.COLOR, colorValue);
    }
    
    return value;
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.COLOR;
  }
  
}
