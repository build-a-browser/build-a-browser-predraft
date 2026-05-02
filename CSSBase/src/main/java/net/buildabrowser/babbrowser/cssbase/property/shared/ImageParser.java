package net.buildabrowser.babbrowser.cssbase.property.shared;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;

public class ImageParser implements PropertyValueParser {

  private final URLParser urlParser = new URLParser();

  // TODO: Support gradients
  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return urlParser.parse(stream);
  }

}
