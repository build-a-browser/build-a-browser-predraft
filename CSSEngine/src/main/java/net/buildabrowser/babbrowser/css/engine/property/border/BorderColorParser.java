package net.buildabrowser.babbrowser.css.engine.property.border;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorBaseParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public class BorderColorParser implements PropertyValueParser {

  private final PropertyValueParser innerParser;
  private final CSSProperty relatedProperty;

  public BorderColorParser(CSSProperty property) {
    this.innerParser = new ColorBaseParser();
    this.relatedProperty = property;
  }

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return innerParser.parse(stream);
  }

  @Override
  public CSSProperty relatedProperty() {
    return relatedProperty;
  }

}
