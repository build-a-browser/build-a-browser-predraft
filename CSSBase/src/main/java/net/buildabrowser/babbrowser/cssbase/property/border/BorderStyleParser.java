package net.buildabrowser.babbrowser.cssbase.property.border;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.shared.LineStyleValue;

public class BorderStyleParser implements PropertyValueParser {

  private final CSSProperty relatedProperty;

  public BorderStyleParser(CSSProperty property) {
    this.relatedProperty = property;
  }

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseIdentMap(stream, Map.of(
      "none", CSSValue.NONE,
      "hidden", LineStyleValue.HIDDEN,
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
    return this.relatedProperty;
  }
  
}
