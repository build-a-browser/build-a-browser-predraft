package net.buildabrowser.babbrowser.css.engine.property.whitespace;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;


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
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    CSSValue result = PropertyValueParserUtil.parseIdentMap(stream, COLLAPSE_VALUES);
    if (result.isFailure()) return result;
    if (!(stream.peek() instanceof EOFToken)) return CSSFailure.EXPECTED_EOF;

    activeStyles.setProperty(CSSProperty.WHITE_SPACE_COLLAPSE, result);
    return result;
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.WHITE_SPACE_COLLAPSE;
  }
  
}
