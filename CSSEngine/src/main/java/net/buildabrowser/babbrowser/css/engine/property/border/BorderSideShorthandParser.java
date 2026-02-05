package net.buildabrowser.babbrowser.css.engine.property.border;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil.AnyOrderResult;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorBaseParser;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public class BorderSideShorthandParser implements PropertyValueParser {

  private static final PropertyValueParser widthParser = new BorderSizeParser(null);
  private static final PropertyValueParser colorParser = new ColorBaseParser();
  private static final PropertyValueParser styleParser = new BorderStyleParser(null);

  private final CSSProperty relatedProperty;
  private final CSSProperty widthProperty;
  private final CSSProperty colorProperty;
  private final CSSProperty styleProperty;

  public BorderSideShorthandParser(
    CSSProperty relatedProperty, CSSProperty widthProperty, CSSProperty colorProperty, CSSProperty styleProperty
  ) {
    this.relatedProperty = relatedProperty;
    this.widthProperty = widthProperty;
    this.colorProperty = colorProperty;
    this.styleProperty = styleProperty;
  }

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    CSSValue result = PropertyValueParserUtil.parseAnyOrder(stream, widthParser, colorParser, styleParser);
    if (result.isFailure()) return result;

    CSSValue[] components = ((AnyOrderResult) result).values();
    return new BorderCompositeValue(components[0], components[1], components[2]);
  }

  @Override
  public CSSProperty relatedProperty() {
    return relatedProperty;
  }

  @Override
  public void updateProperty(CSSValue result, ActiveStyles activeStyles) {
    if (!(result instanceof BorderCompositeValue compositeValue)) return;
    if (compositeValue.width() != null) activeStyles.setProperty(widthProperty, compositeValue.width());
    if (compositeValue.color() != null) activeStyles.setProperty(colorProperty, compositeValue.color());
    if (compositeValue.style() != null) activeStyles.setProperty(styleProperty, compositeValue.style());
  }

}
