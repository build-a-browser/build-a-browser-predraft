package net.buildabrowser.babbrowser.cssbase.property.background;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundSizeValue.SizedBackgroundSizeValue;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class BackgroundSizeParser implements PropertyValueParser {

  private final SizeParser bgSizeParser = new SizeParser(false, true, null);

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseCommaRepeat(stream, this::parseInternal);
  }
  
  public CSSValue parseInternal(CSSTokenStream stream) throws IOException {
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

    int mark = stream.mark();
    CSSValue secondValue = bgSizeParser.parse(stream);
    if (secondValue.isFailure()) {
      stream.restoreMark(mark);
      return SizedBackgroundSizeValue.create(firstValue, CSSValue.AUTO);
    }

    stream.discardMark();
    return SizedBackgroundSizeValue.create(firstValue, secondValue);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BACKGROUND_SIZE;
  }

}
