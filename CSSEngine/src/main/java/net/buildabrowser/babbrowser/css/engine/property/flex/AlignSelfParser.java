package net.buildabrowser.babbrowser.css.engine.property.flex;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public class AlignSelfParser implements PropertyValueParser {
  
  private static final Map<String, CSSValue> ALIGN_SELF_VALUES = Map.of(
    "auto", CSSValue.AUTO,
    "flex-start", AlignItemsValue.FLEX_START,
    "flex-end", AlignItemsValue.FLEX_END,
    "center", AlignItemsValue.CENTER,
    "baseline", AlignItemsValue.BASELINE,
    "stretch", AlignItemsValue.STRETCH
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, ALIGN_SELF_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.ALIGN_SELF;
  }

}
