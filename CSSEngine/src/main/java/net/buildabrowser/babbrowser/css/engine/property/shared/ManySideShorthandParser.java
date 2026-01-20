package net.buildabrowser.babbrowser.css.engine.property.shared;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;

public class ManySideShorthandParser implements PropertyValueParser {

  private static final CSSFailure EXPECTED_EOF = new CSSFailure("Expected an EOF token");
  private static final CSSFailure EXPECTED_SIZE = new CSSFailure("Expected at least one size");

  private final PropertyValueParser innerParser;
  private final CSSProperty[] relatedProperties;
  private final CSSProperty primaryProperty;

  public ManySideShorthandParser(PropertyValueParser innerParser, CSSProperty[] relatedProperties, CSSProperty primaryProperty) {
    assert relatedProperties.length == 4;
    this.innerParser = innerParser;
    this.relatedProperties = relatedProperties;
    this.primaryProperty = primaryProperty;
  }

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    // Inhherit should be handled for us

    CSSValue[] consumedValues = new CSSValue[4];
    int i;
    for (i = 0; i < 4; i++) {
      if (stream.peek() instanceof EOFToken) break;
      CSSValue innerResult = innerParser.parse(stream, activeStyles);
      if (innerResult.isFailure()) return innerResult;
      consumedValues[i] = innerResult;
    }
    if (i == 0) {
      return EXPECTED_SIZE;
    } else if (!(stream.peek() instanceof EOFToken)) {
      return EXPECTED_EOF;
    }

    switch (i) {
      case 1:
        consumedValues[3] = consumedValues[0];
        consumedValues[2] = consumedValues[0];
        consumedValues[1] = consumedValues[0];
        break;
      case 2:
        consumedValues[3] = consumedValues[1];
        consumedValues[2] = consumedValues[0];
        break;
      case 3:
        consumedValues[3] = consumedValues[1];
      case 4:
        break;
    }

    for (int j = 0; j < 4; j++) {
      activeStyles.setProperty(relatedProperties[j], consumedValues[j]);
    }

    return new ManySideValue(
      consumedValues[0], consumedValues[1],
      consumedValues[2], consumedValues[3]);
  }
  
  @Override
  public CSSProperty relatedProperty() {
    return this.primaryProperty;
  }

}
