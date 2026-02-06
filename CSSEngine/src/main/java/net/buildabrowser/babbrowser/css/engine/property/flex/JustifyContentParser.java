package net.buildabrowser.babbrowser.css.engine.property.flex;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public class JustifyContentParser implements PropertyValueParser {
  
  private static final Map<String, CSSValue> JUSTIFY_CONTENT_VALUES = Map.of(
    "flex-start", JustifyContentValue.FLEX_START,
    "flex-end", JustifyContentValue.FLEX_END,
    "center", JustifyContentValue.CENTER,
    "space-between", JustifyContentValue.SPACE_BETWEEN,
    "space-around", JustifyContentValue.SPACE_AROUND
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, JUSTIFY_CONTENT_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.JUSTIFY_CONTENT;
  }

}
