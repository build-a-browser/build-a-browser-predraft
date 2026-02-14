package net.buildabrowser.babbrowser.css.engine.property.background;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public class BackgroundAttachmentParser implements PropertyValueParser {

  private static final Map<String, CSSValue> BACKGROUND_ATTACHMENT_VALUES = Map.of(
    "fixed", BackgroundAttachmentValue.FIXED,
    "local", BackgroundAttachmentValue.LOCAL,
    "scroll", BackgroundAttachmentValue.SCROLL
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, BACKGROUND_ATTACHMENT_VALUES);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BACKGROUND_ATTACHMENT;
  }
  
}
