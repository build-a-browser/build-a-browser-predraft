package net.buildabrowser.babbrowser.cssbase.property.outline;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.MutablePropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.AnyOrderResult;
import net.buildabrowser.babbrowser.cssbase.property.shared.LineWidthParser;

public class OutlineShorthandParser implements PropertyValueParser {

  private final LineWidthParser outlineWidthParser = new LineWidthParser(null);
  private final OutlineStyleParser outlineStyleParser = new OutlineStyleParser();
  private final OutlineColorParser outlineColorParser = new OutlineColorParser();

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    CSSValue result = PropertyValueParserUtil.parseAnyOrder(
      stream, outlineWidthParser, outlineStyleParser, outlineColorParser);
    if (result.isFailure()) return result;
    CSSValue[] results = ((AnyOrderResult) result).values();
    CSSValue outlineWidth = results[0];
    CSSValue outlineStyle = results[1];
    CSSValue outlineColor = results[2];
    if (
      outlineStyle == CSSValue.AUTO && outlineColor == null
      || outlineStyle == null && outlineColor == CSSValue.AUTO
    ) {
      outlineStyle = outlineColor = CSSValue.AUTO;
    }

    return new OutlineCompositeValue(outlineWidth, outlineStyle, outlineColor);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.OUTLINE;
  }

  @Override
  public void updateProperty(CSSValue result, MutablePropertyContainer propertySetter) {
    OutlineCompositeValue compositeValue = (OutlineCompositeValue) result;
    if (compositeValue.outlineWidth() != null) {
      propertySetter.setProperty(CSSProperty.OUTLINE_WIDTH, compositeValue.outlineWidth());
    }
    if (compositeValue.outlineStyle() != null) {
      propertySetter.setProperty(CSSProperty.OUTLINE_STYLE, compositeValue.outlineStyle());
    }
    if (compositeValue.outlineColor() != null) {
      propertySetter.setProperty(CSSProperty.OUTLINE_COLOR, compositeValue.outlineColor());
    }
  }
  
}
