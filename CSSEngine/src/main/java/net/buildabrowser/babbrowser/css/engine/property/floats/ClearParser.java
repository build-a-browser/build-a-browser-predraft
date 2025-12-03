package net.buildabrowser.babbrowser.css.engine.property.floats;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public class ClearParser implements PropertyValueParser {

  private static final Map<String, CSSValue> CLEAR_VALUES = Map.of(
    "left", ClearValue.LEFT,
    "right", ClearValue.RIGHT,
    "both", ClearValue.BOTH
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    CSSValue result = PropertyValueParserUtil.parseIdentMap(stream, CLEAR_VALUES);
    if (result.isFailure()) return result;

    activeStyles.setClear(result);
    return result;
  }
  
}
