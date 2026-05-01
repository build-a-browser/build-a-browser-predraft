package net.buildabrowser.babbrowser.cssbase.property.font;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class FontSizeParser implements PropertyValueParser {

  private static final Map<String, CSSValue> NAMED_SIZES = Map.of(
    "xx-small", FontNamedSizeValue.XX_SMALL,
    "x-small", FontNamedSizeValue.X_SMALL,
    "small", FontNamedSizeValue.SMALL,
    "medium", FontNamedSizeValue.MEDIUM,
    "large", FontNamedSizeValue.LARGE,
    "x-large", FontNamedSizeValue.XX_LARGE,
    "xx-large", FontNamedSizeValue.XX_LARGE,
    "xxx-large", FontNamedSizeValue.XXX_LARGE,
    "larger", FontNamedSizeValue.LARGER,
    "smaller", FontNamedSizeValue.SMALLER
  );

  private final SizeParser sizeParser = new SizeParser(false, false, null);

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    if (
      stream.peek() instanceof IdentToken identToken
      && NAMED_SIZES.containsKey(identToken.value())
    ) {
      stream.read();
      return NAMED_SIZES.get(identToken.value());
    } else {
      return sizeParser.parse(stream);
    }
    // TODO: Math
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.FONT_SIZE;
  }
  
}
