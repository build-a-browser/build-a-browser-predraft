package net.buildabrowser.babbrowser.cssbase.property.text;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;

public class TextAlignParser implements PropertyValueParser {

  private static final Map<String, CSSValue> TEXT_ALIGN_VALUES = Map.of(
    "start", TextAlignValue.START,
    "end", TextAlignValue.END,
    "left", TextAlignValue.LEFT,
    "right", TextAlignValue.RIGHT,
    "center", TextAlignValue.CENTER,
    "justify", TextAlignValue.JUSTIFY,
    "justify-all", TextAlignValue.JUSTIFY_ALL,
    "match-parent", TextAlignValue.MATCH_PARENT
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, TEXT_ALIGN_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.TEXT_ALIGN;
  }
  
}
