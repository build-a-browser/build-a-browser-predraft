package net.buildabrowser.babbrowser.css.engine.property.border;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;

public class BorderStyleParser implements PropertyValueParser {

  private final CSSProperty relatedProperty;

  public BorderStyleParser(CSSProperty property) {
    this.relatedProperty = property;
  }

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    CSSValue result = parseInternal(stream, activeStyles);
    if (result.isFailure()) return result;
    if (!(stream.peek() instanceof EOFToken)) return CSSFailure.EXPECTED_EOF;

    activeStyles.setProperty(this.relatedProperty, result);
    return result;
  }

  @Override
  public CSSProperty relatedProperty() {
    return this.relatedProperty;
  }

  public CSSValue parseInternal(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
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
  
}
