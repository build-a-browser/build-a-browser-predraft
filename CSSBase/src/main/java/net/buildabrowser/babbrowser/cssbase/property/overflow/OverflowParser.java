package net.buildabrowser.babbrowser.cssbase.property.overflow;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;

public class OverflowParser implements PropertyValueParser {

  private static final Map<String, CSSValue> OVERFLOW_VALUES = Map.of(
    "visible", OverflowValue.VISIBLE,
    "hidden", OverflowValue.HIDDEN,
    "clip", OverflowValue.CLIP,
    "scroll", OverflowValue.SCROLL,
    "auto", CSSValue.AUTO,
    "overlay", CSSValue.AUTO
  );

  private final CSSProperty relatedProperty;

  public OverflowParser(CSSProperty relatedProperty) {
    this.relatedProperty = relatedProperty;
  }

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, OVERFLOW_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return this.relatedProperty;
  }
  
}
