package net.buildabrowser.babbrowser.css.engine.property.background;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.property.background.BackgroundSizeValue.SizedBackgroundSizeValue;
import net.buildabrowser.babbrowser.css.engine.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class BackgroundSizeParser implements PropertyValueParser {

  private final SizeParser bgSizeParser = new SizeParser(false, true, null);

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseCommaRepeat(stream, this::parseInternal);
  }
  
  public CSSValue parseInternal(SeekableCSSTokenStream stream) throws IOException {
    if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("cover")
    ) {
      stream.read();
      return BackgroundSizeValue.COVER;
    } else if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("contain")
    ) {
      stream.read();
      return BackgroundSizeValue.CONTAIN;
    }

    CSSValue firstValue = bgSizeParser.parse(stream);
    if (firstValue.isFailure()) return firstValue;

    CSSValue secondValue = bgSizeParser.parse(stream);
    int position = stream.position();
    if (secondValue.isFailure()) {
      stream.seek(position);
      return SizedBackgroundSizeValue.create(firstValue, CSSValue.AUTO);
    }

    return SizedBackgroundSizeValue.create(firstValue, secondValue);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BACKGROUND_SIZE;
  }

}
