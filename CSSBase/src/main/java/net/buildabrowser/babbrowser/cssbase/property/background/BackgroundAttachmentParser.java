package net.buildabrowser.babbrowser.cssbase.property.background;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;

public class BackgroundAttachmentParser implements PropertyValueParser {

  private static final Map<String, CSSValue> BACKGROUND_ATTACHMENT_VALUES = Map.of(
    "fixed", BackgroundAttachmentValue.FIXED,
    "local", BackgroundAttachmentValue.LOCAL,
    "scroll", BackgroundAttachmentValue.SCROLL
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseCommaRepeat(stream, this::parseInternal);
  }

  public CSSValue parseInternal(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, BACKGROUND_ATTACHMENT_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BACKGROUND_ATTACHMENT;
  }
  
}
