package net.buildabrowser.babbrowser.cssbase.property.overflow;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;

public class OverflowShorthandParser implements PropertyValueParser {

  private final OverflowParser overflowParser = new OverflowParser(null);

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    CSSValue xOverflow = overflowParser.parse(stream);
    if (xOverflow.isFailure()) return xOverflow;
    if (stream.peek() instanceof EOFToken) {
      return OverflowShorthandValue.create(xOverflow, xOverflow);
    }

    CSSValue yOverflow = overflowParser.parse(stream);
    if (yOverflow.isFailure()) return yOverflow;
    return OverflowShorthandValue.create(xOverflow, yOverflow);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.OVERFLOW;
  }

  @Override
  public void updateProperty(CSSValue result, PropertyContainer propertySetter) {
    OverflowShorthandValue shorthandValue = (OverflowShorthandValue) result;
    propertySetter.setProperty(CSSProperty.OVERFLOW_X, shorthandValue.overflowX());
    propertySetter.setProperty(CSSProperty.OVERFLOW_Y, shorthandValue.overflowY());
  }
  
}
