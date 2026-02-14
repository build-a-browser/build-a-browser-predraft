package net.buildabrowser.babbrowser.css.engine.property.background;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.property.background.BackgroundSizeValue.SizedBackgroundSizeValue;
import net.buildabrowser.babbrowser.css.engine.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class BackgroundSizeParser implements PropertyValueParser {

  private final SizeParser bgSizeParser = new SizeParser(false, true, null);

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseCommaRepeat(stream, this::parseInner);
  }
  
  private CSSValue parseInner(SeekableCSSTokenStream stream) throws IOException {
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

    Token nextToken = stream.peek();
    if (
      nextToken instanceof EOFToken
      || nextToken instanceof CommaToken
    ) {
      return SizedBackgroundSizeValue.create(firstValue, CSSValue.AUTO);
    }

    CSSValue secondValue = bgSizeParser.parse(stream);
    if (secondValue.isFailure()) return secondValue;

    return SizedBackgroundSizeValue.create(firstValue, secondValue);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BACKGROUND_SIZE;
  }

}
