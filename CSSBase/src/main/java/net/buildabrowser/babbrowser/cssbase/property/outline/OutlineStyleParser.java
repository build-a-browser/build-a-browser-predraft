package net.buildabrowser.babbrowser.cssbase.property.outline;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.shared.LineStyleValue;

public class OutlineStyleParser implements PropertyValueParser {

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, Map.of(
      "none", CSSValue.NONE,
      "auto", CSSValue.AUTO,
      "dotted", LineStyleValue.DOTTED,
      "dashed", LineStyleValue.DASHED,
      "solid", LineStyleValue.SOLID,
      "double", LineStyleValue.DOUBLE,
      "groove", LineStyleValue.GROOVE,
      "ridge", LineStyleValue.RIDGE,
      "inset", LineStyleValue.INSET,
      "outset", LineStyleValue.OUTSET
    ));
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.OUTLINE_STYLE;
  }
  
}
