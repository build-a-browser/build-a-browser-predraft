package net.buildabrowser.babbrowser.css.engine.property.border;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public class BorderStyleParser implements PropertyValueParser {

  private final CSSProperty relatedProperty;

  public BorderStyleParser(CSSProperty property) {
    this.relatedProperty = property;
  }

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, Map.of(
      "none", CSSValue.NONE,
      "hidden", BorderStyleValue.HIDDEN,
      "dotted", BorderStyleValue.DOTTED,
      "dashed", BorderStyleValue.DASHED,
      "solid", BorderStyleValue.SOLID,
      "double", BorderStyleValue.DOUBLE,
      "groove", BorderStyleValue.GROOVE,
      "ridge", BorderStyleValue.RIDGE,
      "inset", BorderStyleValue.INSET,
      "outset", BorderStyleValue.OUTSET
    ));
  }

  @Override
  public CSSProperty relatedProperty() {
    return this.relatedProperty;
  }
  
}
