package net.buildabrowser.babbrowser.css.engine.property.shared;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public class ImageParser implements PropertyValueParser {

  private final URLParser urlParser = new URLParser();

  // TODO: Support gradients
  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return urlParser.parse(stream);
  }

}
