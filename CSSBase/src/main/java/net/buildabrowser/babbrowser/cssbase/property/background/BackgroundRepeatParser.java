package net.buildabrowser.babbrowser.cssbase.property.background;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundRepeatValue.BackgroundAxisRepeatValue;

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
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseCommaRepeat(stream, this::parseInternal);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BACKGROUND_REPEAT;
  }

  public CSSValue parseInternal(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseLongest(stream,
      stream1 -> PropertyValueParserUtil.parseIdentMap(stream1, OUTER_VALUES),
      this::parseInnerValues);
  }

  private CSSValue parseInnerValues(CSSTokenStream stream) throws IOException {
    CSSValue firstValue = PropertyValueParserUtil.parseIdentMap(stream, INNER_VALUES);
    if (firstValue.isFailure()) return firstValue;

    int mark = stream.mark();
    CSSValue secondValue = PropertyValueParserUtil.parseIdentMap(stream, INNER_VALUES);
    if (secondValue.isFailure()) {
      secondValue = firstValue;
      stream.restoreMark(mark);
    } else {
      stream.discardMark();
    }

    return BackgroundRepeatValue.create(
      (BackgroundAxisRepeatValue) firstValue,
      (BackgroundAxisRepeatValue) secondValue);
  }
  
}
