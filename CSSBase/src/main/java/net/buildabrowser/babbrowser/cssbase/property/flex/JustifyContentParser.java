package net.buildabrowser.babbrowser.cssbase.property.flex;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;

public class JustifyContentParser implements PropertyValueParser {
  
  private static final Map<String, CSSValue> JUSTIFY_CONTENT_VALUES = Map.of(
    "flex-start", JustifyContentValue.FLEX_START,
    "flex-end", JustifyContentValue.FLEX_END,
    "center", JustifyContentValue.CENTER,
    "space-between", JustifyContentValue.SPACE_BETWEEN,
    "space-around", JustifyContentValue.SPACE_AROUND
  );

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, JUSTIFY_CONTENT_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.JUSTIFY_CONTENT;
  }

}
