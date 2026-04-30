package net.buildabrowser.babbrowser.css.engine.property.background;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.property.shared.ImageParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class BackgroundImageParser implements PropertyValueParser {

  private final ImageParser imageParser = new ImageParser();

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseCommaRepeat(stream, this::parseInternal);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BACKGROUND_IMAGE;
  }

  public CSSValue parseInternal(SeekableCSSTokenStream stream) throws IOException {
    if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("none")
    ) {
      stream.read();
      return CSSValue.NONE;
    } else {
      CSSValue result = imageParser.parse(stream);
      return result;
    }
  }
  
}
