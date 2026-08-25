package net.buildabrowser.babbrowser.cssbase.property.align;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;

public class AlignSelfParser implements PropertyValueParser {
  
  private static final Map<String, CSSValue> ALIGN_SELF_VALUES = Map.of(
    "auto", CSSValue.AUTO,
    "self-start", AlignItemsValue.SELF_START,
    "self-end", AlignItemsValue.SELF_END,
    "flex-start", AlignItemsValue.FLEX_START,
    "flex-end", AlignItemsValue.FLEX_END,
    "center", AlignItemsValue.CENTER,
    "baseline", AlignItemsValue.BASELINE,
    "stretch", AlignItemsValue.STRETCH
  );

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, ALIGN_SELF_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.ALIGN_SELF;
  }

}
