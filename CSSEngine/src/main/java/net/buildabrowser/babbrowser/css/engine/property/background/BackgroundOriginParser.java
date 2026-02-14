package net.buildabrowser.babbrowser.css.engine.property.background;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.box.VisualBoxParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public class BackgroundOriginParser implements PropertyValueParser {

  private final VisualBoxParser visualBoxParser = new VisualBoxParser();

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return visualBoxParser.parse(stream);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BACKGROUND_ORIGIN;
  }
  
}
