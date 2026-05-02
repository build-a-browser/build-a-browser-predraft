package net.buildabrowser.babbrowser.cssbase.property.size;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;

public class BoxSizingParser implements PropertyValueParser {
  
  private static final Map<String, CSSValue> BOX_SIZING_VALUES = Map.of(
    "content-box", BoxSizingValue.CONTENT_BOX,
    "border-box", BoxSizingValue.BORDER_BOX
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, BOX_SIZING_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BOX_SIZING;
  }

}
