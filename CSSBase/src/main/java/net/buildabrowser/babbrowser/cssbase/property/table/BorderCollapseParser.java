package net.buildabrowser.babbrowser.cssbase.property.table;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;

public class BorderCollapseParser implements PropertyValueParser {

  private static final Map<String, CSSValue> BORDER_COLLAPSE_VALUES = Map.of(
    "seperate", BorderCollapseValue.SEPARATE,
    "collapse", BorderCollapseValue.COLLAPSE
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, BORDER_COLLAPSE_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BORDER_COLLAPSE;
  }
  
}
