package net.buildabrowser.babbrowser.cssbase.property.background;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.shared.ImageParser;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class BackgroundImageParser implements PropertyValueParser {

  private final ImageParser imageParser = new ImageParser();

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseCommaRepeat(stream, this::parseInternal);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BACKGROUND_IMAGE;
  }

  public CSSValue parseInternal(CSSTokenStream stream) throws IOException {
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
