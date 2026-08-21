package net.buildabrowser.babbrowser.cssbase.property.align;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.MutablePropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;

public class GapShorthandParser implements PropertyValueParser {

  private final GapParser gapParser = new GapParser(null);

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    CSSValue rowGap = gapParser.parse(stream);
    if (rowGap.isFailure()) return rowGap;

    if (stream.peek() instanceof EOFToken) {
      return new GapShorthandValue(rowGap, rowGap);
    }

    CSSValue columnGap = gapParser.parse(stream);
    if (columnGap.isFailure()) return columnGap;
    return new GapShorthandValue(rowGap, columnGap);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.GAP;
  }

  @Override
  public void updateProperty(CSSValue result, MutablePropertyContainer propertySetter) {
    propertySetter.setProperty(CSSProperty.ROW_GAP, ((GapShorthandValue) result).rowGap());
    propertySetter.setProperty(CSSProperty.COLUMN_GAP, ((GapShorthandValue) result).columnGap());
  }
  
}
