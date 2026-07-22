package net.buildabrowser.babbrowser.cssbase.property.visibility;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;

public class VisibilityParser implements PropertyValueParser {

  private static final Map<String, CSSValue> VISIBILITY_VALUES = Map.of(
    "visible", VisibilityValue.VISIBLE,
    "hidden", VisibilityValue.HIDDEN,
    "collapse", VisibilityValue.COLLAPSE
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, VISIBILITY_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.VISIBILITY;
  }
  
}
