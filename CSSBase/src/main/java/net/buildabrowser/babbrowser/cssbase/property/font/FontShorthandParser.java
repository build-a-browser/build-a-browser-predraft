package net.buildabrowser.babbrowser.cssbase.property.font;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.AnyOrderResult;
import net.buildabrowser.babbrowser.cssbase.property.text.LineHeightParser;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;

public class FontShorthandParser implements PropertyValueParser {

  private final FontWeightParser fontWeightParser = new FontWeightParser();
  private final FontSizeParser fontSizeParser = new FontSizeParser();
  private final LineHeightParser lineHeightParser = new LineHeightParser();
  private final FontFamilyParser fontFamilyParser = new FontFamilyParser();

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    // TODO: Expand this with the other possible options
    CSSValue anyOrderResult = PropertyValueParserUtil.parseAnyOrder(stream, new PropertyValueParser[] {
      fontWeightParser
    });
    
    CSSValue fontWeightValue = null;
    if (!anyOrderResult.isFailure()) {
      CSSValue[] aoValues = ((AnyOrderResult) anyOrderResult).values();
      fontWeightValue = aoValues[0];
    }

    CSSValue fontSizeValue = fontSizeParser.parse(stream);
    if (fontSizeValue.isFailure()) return fontSizeValue;

    CSSValue lineHeightValue = null;
    if (
      stream.peek() instanceof DelimToken delimToken
      && delimToken.ch() == '/'
    ) {
      stream.read();
      lineHeightValue = lineHeightParser.parse(stream);
      if (lineHeightValue.isFailure()) return lineHeightValue;
    }

    CSSValue fontFamilyValue = fontFamilyParser.parse(stream);
    if (fontFamilyValue.isFailure()) return fontFamilyValue;

    return new FontShorthandValue(fontWeightValue, fontSizeValue, lineHeightValue, fontFamilyValue);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.FONT;
  }

  @Override
  public void updateProperty(CSSValue result, PropertyContainer propertySetter) {
    FontShorthandValue shorthand = (FontShorthandValue) result;
    if (shorthand.fontWeight() != null) {
      propertySetter.setProperty(CSSProperty.FONT_WEIGHT, shorthand.fontWeight());
    }
    propertySetter.setProperty(CSSProperty.FONT_SIZE, shorthand.fontSize());
    if (shorthand.lineHeight() != null) {
      propertySetter.setProperty(CSSProperty.LINE_HEIGHT, shorthand.lineHeight());
    }
    propertySetter.setProperty(CSSProperty.FONT_FAMILY, shorthand.fontFamily());
  }
  
}
