package net.buildabrowser.babbrowser.css.engine.property.border;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil.AnyOrderResult;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorBaseParser;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;

public class BorderShorthandParser implements PropertyValueParser {
  
  private static final CSSFailure EXPECTED_EOF = new CSSFailure("Expected an EOF token");

  private static final PropertyValueParser widthParser = new BorderSizeParser(null)::parseInternal;
  private static final PropertyValueParser colorParser = new ColorBaseParser();
  private static final PropertyValueParser styleParser = new BorderStyleParser(null)::parseInternal;

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    CSSValue result = PropertyValueParserUtil.parseAnyOrder(stream, widthParser, colorParser, styleParser);
    if (result.isFailure()) return result;

    if (!(stream.peek() instanceof EOFToken)) {
      return EXPECTED_EOF;
    }

    CSSValue[] components = ((AnyOrderResult) result).values();
    if (components[0] != null) {
      activeStyles.setProperty(CSSProperty.BORDER_TOP_WIDTH, components[0]);
      activeStyles.setProperty(CSSProperty.BORDER_BOTTOM_WIDTH, components[0]);
      activeStyles.setProperty(CSSProperty.BORDER_LEFT_WIDTH, components[0]);
      activeStyles.setProperty(CSSProperty.BORDER_RIGHT_WIDTH, components[0]);
    }
    if (components[1] != null) {
      activeStyles.setProperty(CSSProperty.BORDER_TOP_COLOR, components[1]);
      activeStyles.setProperty(CSSProperty.BORDER_BOTTOM_COLOR, components[1]);
      activeStyles.setProperty(CSSProperty.BORDER_LEFT_COLOR, components[1]);
      activeStyles.setProperty(CSSProperty.BORDER_RIGHT_COLOR, components[1]);
    }
    if (components[2] != null) {
      activeStyles.setProperty(CSSProperty.BORDER_TOP_STYLE, components[2]);
      activeStyles.setProperty(CSSProperty.BORDER_BOTTOM_STYLE, components[2]);
      activeStyles.setProperty(CSSProperty.BORDER_LEFT_STYLE, components[2]);
      activeStyles.setProperty(CSSProperty.BORDER_RIGHT_STYLE, components[2]);
    }
    
    return new BorderCompositeValue(components[0], components[1], components[2]);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BORDER;
  }

}
