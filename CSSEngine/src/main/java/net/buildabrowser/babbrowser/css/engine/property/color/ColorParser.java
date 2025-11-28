package net.buildabrowser.babbrowser.css.engine.property.color;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class ColorParser implements PropertyValueParser {

  private static final Map<String, Integer> COLOR_MAP = Map.of(
    "red", 0xFFFF0000,
    "green", 0xFF00FF00,
    "blue", 0xFF0000FF
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    IdentToken identToken = (IdentToken) stream.read();
    Integer color = COLOR_MAP.get(identToken.value());
    if (color != null) {
      activeStyles.setTextColor(color);
      return CSSValue.SUCCESS;
    }

    return new CSSFailure("Color is not valid!");
  }
  
}
