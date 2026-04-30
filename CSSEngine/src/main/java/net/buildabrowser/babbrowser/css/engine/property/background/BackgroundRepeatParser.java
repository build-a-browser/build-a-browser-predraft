package net.buildabrowser.babbrowser.css.engine.property.background;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.property.background.BackgroundRepeatValue.BackgroundAxisRepeatValue;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;

public class BackgroundRepeatParser implements PropertyValueParser {

  private static final Map<String, CSSValue> OUTER_VALUES = Map.of(
    "repeat-x", BackgroundRepeatValue.create(
      BackgroundAxisRepeatValue.REPEAT, BackgroundAxisRepeatValue.NO_REPEAT),
    "repeat-y", BackgroundRepeatValue.create(
      BackgroundAxisRepeatValue.NO_REPEAT, BackgroundAxisRepeatValue.REPEAT)
  );

  private static final Map<String, CSSValue> INNER_VALUES = Map.of(
    "repeat", BackgroundAxisRepeatValue.REPEAT,
    "space", BackgroundAxisRepeatValue.SPACE,
    "round", BackgroundAxisRepeatValue.ROUND,
    "no-repeat", BackgroundAxisRepeatValue.NO_REPEAT
  );

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseCommaRepeat(stream, this::parseInternal);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BACKGROUND_REPEAT;
  }

  public CSSValue parseInternal(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseLongest(stream,
      stream1 -> PropertyValueParserUtil.parseIdentMap(stream1, OUTER_VALUES),
      this::parseInnerValues);
  }

  private CSSValue parseInnerValues(SeekableCSSTokenStream stream) throws IOException {
    CSSValue firstValue = PropertyValueParserUtil.parseIdentMap(stream, INNER_VALUES);
    if (firstValue.isFailure()) return firstValue;

    if (stream.peek() instanceof EOFToken) {
      return BackgroundRepeatValue.create(
        (BackgroundAxisRepeatValue) firstValue,
        (BackgroundAxisRepeatValue) firstValue);
    }

    CSSValue secondValue = PropertyValueParserUtil.parseIdentMap(stream, INNER_VALUES);
    if (secondValue.isFailure()) return secondValue;

    return BackgroundRepeatValue.create(
      (BackgroundAxisRepeatValue) firstValue,
      (BackgroundAxisRepeatValue) secondValue);
  }
  
}
