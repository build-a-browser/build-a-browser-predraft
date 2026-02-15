package net.buildabrowser.babbrowser.cssbase.property.color;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;

public class ColorParser implements PropertyValueParser {

  private final PropertyValueParser colorBaseParser = new ColorBaseParser();

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return colorBaseParser.parse(stream);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.COLOR;
  }
  
}
