package net.buildabrowser.babbrowser.cssbase.property.whitespace;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;


public class WhitespaceCollapseValueParser implements PropertyValueParser {

  private static final Map<String, CSSValue> COLLAPSE_VALUES = Map.of(
    "collapse", WhitespaceCollapseValue.COLLAPSE,
    "discard", WhitespaceCollapseValue.DISCARD,
    "preserve", WhitespaceCollapseValue.PRESERVE,
    "preserve-breaks", WhitespaceCollapseValue.PRESERVE_BREAKS,
    "preserve-spaces", WhitespaceCollapseValue.PRESERVE_SPACES,
    "break-spaces", WhitespaceCollapseValue.PRESERVE_SPACES
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, COLLAPSE_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.WHITE_SPACE_COLLAPSE;
  }
  
}
