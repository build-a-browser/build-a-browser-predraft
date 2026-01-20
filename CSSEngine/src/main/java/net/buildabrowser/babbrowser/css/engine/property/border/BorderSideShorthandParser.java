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

public class BorderSideShorthandParser implements PropertyValueParser {
  
  private static final CSSFailure EXPECTED_EOF = new CSSFailure("Expected an EOF token");

  private static final PropertyValueParser widthParser = new BorderSizeParser(null)::parseInternal;
  private static final PropertyValueParser styleParser = new BorderStyleParser(null)::parseInternal;
  private static final PropertyValueParser colorParser = new ColorBaseParser();

  private final CSSProperty relatedProperty;
  private final CSSProperty widthProperty;
  private final CSSProperty styleProperty;
  private final CSSProperty colorProperty;

  public BorderSideShorthandParser(
    CSSProperty relatedProperty, CSSProperty widthProperty, CSSProperty styleProperty, CSSProperty colorProperty
  ) {
    this.relatedProperty = relatedProperty;
    this.widthProperty = widthProperty;
    this.styleProperty = styleProperty;
    this.colorProperty = colorProperty;
  }

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    CSSValue result = PropertyValueParserUtil.parseAnyOrder(stream, widthParser, styleParser, colorParser);
    if (result.isFailure()) return result;

    if (!(stream.peek() instanceof EOFToken)) {
      return EXPECTED_EOF;
    }

    CSSValue[] components = ((AnyOrderResult) result).values();
    if (components[0] != null) activeStyles.setProperty(widthProperty, components[0]);
    if (components[1] != null) activeStyles.setProperty(styleProperty, components[1]);
    if (components[2] != null) activeStyles.setProperty(colorProperty, components[2]);
    
    return new BorderCompositeValue(components[0], components[1], components[2]);
  }

  @Override
  public CSSProperty relatedProperty() {
    return relatedProperty;
  }

}
