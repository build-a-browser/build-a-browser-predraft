package net.buildabrowser.babbrowser.cssbase.property.flex;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;

public class AlignContentParser implements PropertyValueParser {
  
  private static final Map<String, CSSValue> ALIGN_CONTENT_VALUES = Map.of(
    "flex-start", AlignContentValue.FLEX_START,
    "flex-end", AlignContentValue.FLEX_END,
    "center", AlignContentValue.CENTER,
    "space-between", AlignContentValue.SPACE_BETWEEN,
    "space-around", AlignContentValue.SPACE_AROUND,
    "stretch", AlignContentValue.STRETCH
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, ALIGN_CONTENT_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.ALIGN_CONTENT;
  }

}
