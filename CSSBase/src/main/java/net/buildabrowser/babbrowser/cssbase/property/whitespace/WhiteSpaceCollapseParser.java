package net.buildabrowser.babbrowser.cssbase.property.whitespace;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;


public class WhiteSpaceCollapseParser implements PropertyValueParser {

  private static final Map<String, CSSValue> COLLAPSE_VALUES = Map.of(
    "collapse", WhiteSpaceCollapseValue.COLLAPSE,
    "discard", WhiteSpaceCollapseValue.DISCARD,
    "preserve", WhiteSpaceCollapseValue.PRESERVE,
    "preserve-breaks", WhiteSpaceCollapseValue.PRESERVE_BREAKS,
    "preserve-spaces", WhiteSpaceCollapseValue.PRESERVE_SPACES,
    "break-spaces", WhiteSpaceCollapseValue.PRESERVE_SPACES
  );

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, COLLAPSE_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.WHITE_SPACE_COLLAPSE;
  }
  
}
