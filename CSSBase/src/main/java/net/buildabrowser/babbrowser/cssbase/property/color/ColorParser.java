package net.buildabrowser.babbrowser.cssbase.property.color;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;

public class ColorParser implements PropertyValueParser {

  private final PropertyValueParser colorBaseParser = new ColorBaseParser();

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return colorBaseParser.parse(stream);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.COLOR;
  }
  
}
