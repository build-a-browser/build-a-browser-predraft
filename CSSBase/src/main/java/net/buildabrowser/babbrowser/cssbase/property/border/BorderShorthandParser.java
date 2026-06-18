package net.buildabrowser.babbrowser.cssbase.property.border;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.MutablePropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.AnyOrderResult;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorBaseParser;
import net.buildabrowser.babbrowser.cssbase.property.shared.LineWidthParser;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;

public class BorderShorthandParser implements PropertyValueParser {

  private static final PropertyValueParser widthParser = new LineWidthParser(null);
  private static final PropertyValueParser colorParser = new ColorBaseParser();
  private static final PropertyValueParser styleParser = new BorderStyleParser(null);

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    CSSValue result = PropertyValueParserUtil.parseAnyOrder(stream, widthParser, colorParser, styleParser);
    if (result.isFailure()) return result;
    if (!(stream.peek() instanceof EOFToken)) return CSSFailure.EXPECTED_EOF;

    CSSValue[] components = ((AnyOrderResult) result).values();
    
    
    return new BorderCompositeValue(components[0], components[1], components[2]);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BORDER;
  }

  @Override
  public void updateProperty(CSSValue result, MutablePropertyContainer propertySetter) {
    if (!(result instanceof BorderCompositeValue compositeValue)) return;
    CSSValue widthValue = compositeValue.width();
    CSSValue colorValue = compositeValue.color();
    CSSValue styleValue = compositeValue.style();
    if (widthValue != null) {
      propertySetter.setProperty(CSSProperty.BORDER_TOP_WIDTH, widthValue);
      propertySetter.setProperty(CSSProperty.BORDER_BOTTOM_WIDTH, widthValue);
      propertySetter.setProperty(CSSProperty.BORDER_LEFT_WIDTH, widthValue);
      propertySetter.setProperty(CSSProperty.BORDER_RIGHT_WIDTH, widthValue);
    }
    if (colorValue != null) {
      propertySetter.setProperty(CSSProperty.BORDER_TOP_COLOR, colorValue);
      propertySetter.setProperty(CSSProperty.BORDER_BOTTOM_COLOR, colorValue);
      propertySetter.setProperty(CSSProperty.BORDER_LEFT_COLOR, colorValue);
      propertySetter.setProperty(CSSProperty.BORDER_RIGHT_COLOR, colorValue);
    }
    if (styleValue != null) {
      propertySetter.setProperty(CSSProperty.BORDER_TOP_STYLE, styleValue);
      propertySetter.setProperty(CSSProperty.BORDER_BOTTOM_STYLE, styleValue);
      propertySetter.setProperty(CSSProperty.BORDER_LEFT_STYLE, styleValue);
      propertySetter.setProperty(CSSProperty.BORDER_RIGHT_STYLE, styleValue);
    }
  }

}
