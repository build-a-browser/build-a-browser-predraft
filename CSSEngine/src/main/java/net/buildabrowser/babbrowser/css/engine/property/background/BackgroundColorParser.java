package net.buildabrowser.babbrowser.css.engine.property.background;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorBaseParser;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public class BackgroundColorParser implements PropertyValueParser {

  private final PropertyValueParser colorBaseParser = new ColorBaseParser();

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    CSSValue value = colorBaseParser.parse(stream, null);

    if (value instanceof ColorValue colorValue) {
      activeStyles.setBackgroundColor(colorValue.asSARGB());
    }
    
    return value;
  }
  
}
