package net.buildabrowser.babbrowser.cssbase.property.table;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;

public class BorderSpacingParser implements PropertyValueParser {
  
  private final SizeParser sizeParser = new SizeParser(
    false, false, false, false, null);

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    CSSValue hValue = sizeParser.parse(stream);
    if (hValue.isFailure()) return hValue;
    // TODO: Check not negative

    if (stream.peek() instanceof EOFToken) {
      return new BorderSpacingValue(hValue, hValue);
    }

    CSSValue vValue = sizeParser.parse(stream);
    if (vValue.isFailure()) return vValue;

    return new BorderSpacingValue(hValue, vValue);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BORDER_SPACING;
  }

}
