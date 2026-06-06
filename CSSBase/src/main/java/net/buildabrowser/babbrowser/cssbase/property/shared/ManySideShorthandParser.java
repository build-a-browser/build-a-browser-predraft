package net.buildabrowser.babbrowser.cssbase.property.shared;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.MutablePropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;

public class ManySideShorthandParser implements PropertyValueParser {

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
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    // Inhherit should be handled for us

    CSSValue[] consumedValues = new CSSValue[4];
    int i;
    for (i = 0; i < 4; i++) {
      if (stream.peek() instanceof EOFToken) break;
      CSSValue innerResult = innerParser.parse(stream);
      if (innerResult.isFailure()) return innerResult;
      consumedValues[i] = innerResult;
    }
    if (i == 0) {
      return EXPECTED_SIZE;
    } else if (!(stream.peek() instanceof EOFToken)) {
      return CSSFailure.EXPECTED_EOF;
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

    return new ManySideValue(
      consumedValues[0], consumedValues[1],
      consumedValues[2], consumedValues[3]);
  }
  
  @Override
  public CSSProperty relatedProperty() {
    return this.primaryProperty;
  }

  @Override
  public void updateProperty(CSSValue result, MutablePropertyContainer propertySetter) {
    if (!(result instanceof ManySideValue sides)) return;
    propertySetter.setProperty(relatedProperties[0], sides.top());
    propertySetter.setProperty(relatedProperties[1], sides.right());
    propertySetter.setProperty(relatedProperties[2], sides.bottom());
    propertySetter.setProperty(relatedProperties[3], sides.left());
  }

}
