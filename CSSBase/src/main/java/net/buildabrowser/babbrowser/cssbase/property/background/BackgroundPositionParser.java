package net.buildabrowser.babbrowser.cssbase.property.background;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundPositionValue.BackgroundPositionSide;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class BackgroundPositionParser implements PropertyValueParser {

  private static final CSSFailure EXPECTED_SIDE = new CSSFailure("Expected a side!");
  private static final CSSFailure MIXED_SIDES = new CSSFailure("Mixed horizontal/vertical sides!");
  
  private static final PercentageValue ZERO_PERCENT = PercentageValue.create(0);

  private static final Map<String, CSSValue> BACKGROUND_POSITION_VALUES = Map.of(
    "left", BackgroundPositionSide.LEFT,
    "center", BackgroundPositionSide.CENTER,
    "right", BackgroundPositionSide.RIGHT,
    "top", BackgroundPositionSide.TOP,
    "bottom", BackgroundPositionSide.BOTTOM
  );

  private SizeParser sizeParser = new SizeParser(false, false, null);

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseCommaRepeat(stream, this::parseInternal);
  }

  public CSSValue parseInternal(CSSTokenStream stream) throws IOException {
    CSSValue[] values = new CSSValue[4];
    for (int i = 0; i < 4; i++) {
      CSSValue decodedValue = decodeValue(stream);
      if (decodedValue == null) break;
      if (decodedValue.isFailure()) return decodedValue;
      values[i] = decodedValue;
    }

    if (values[0] == null) {
      return EXPECTED_SIDE;
    }

    if (values[1] == null) {
      return parseSingleValue(values);
    } else if (values[2] == null) {
      return parseDoubleValue(values);
    } else {
      return parse34Value(values);
    }
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.BACKGROUND_POSITION;
  }

  private CSSValue parseSingleValue(CSSValue[] values) {
    if (values[0] instanceof BackgroundPositionSide side) {
      return side.isHorizontal() ?
        BackgroundPositionValue.create(
          side, ZERO_PERCENT, BackgroundPositionSide.CENTER, ZERO_PERCENT) :
        BackgroundPositionValue.create(
          BackgroundPositionSide.CENTER, ZERO_PERCENT, side, ZERO_PERCENT);
    } else {
      return BackgroundPositionValue.create(
          BackgroundPositionSide.LEFT, values[0],
          BackgroundPositionSide.CENTER, ZERO_PERCENT);
    }
  }

  private CSSValue parseDoubleValue(CSSValue[] values) {
    boolean hasPercent = false;
    BackgroundPositionSide horizontalSide = BackgroundPositionSide.LEFT;
    CSSValue horizontalLength = ZERO_PERCENT;
    if (values[0] instanceof BackgroundPositionSide side) {
      horizontalSide = side;
    } else {
      hasPercent = true;
      horizontalLength = values[0];
    }

    BackgroundPositionSide verticalSide = BackgroundPositionSide.TOP;
    CSSValue verticalLength = ZERO_PERCENT;
    if (values[1] instanceof BackgroundPositionSide side) {
      verticalSide = side;
    } else {
      hasPercent = true;
      verticalLength = values[1];
    }

    if (hasPercent) {
      if (!(horizontalSide.isHorizontal() && verticalSide.isVertical())) {
        return MIXED_SIDES;
      }
      return BackgroundPositionValue.create(horizontalSide, horizontalLength, verticalSide, verticalLength);
    } else {
      return !horizontalSide.isHorizontal() || !verticalSide.isVertical() ?
        BackgroundPositionValue.create(verticalSide, verticalLength, horizontalSide, horizontalLength) :
        BackgroundPositionValue.create(horizontalSide, horizontalLength, verticalSide, verticalLength);
    }  
  }

  private CSSValue parse34Value(CSSValue[] values) {
    int i = 0;
    if (!(values[i++] instanceof BackgroundPositionSide firstSide)) {
      return EXPECTED_SIDE;
    }

    CSSValue firstLength = ZERO_PERCENT;
    if (!(values[i] instanceof BackgroundPositionSide)) {
      firstLength = values[i];
      i++;
    }

    if (!(values[i++] instanceof BackgroundPositionSide secondSide)) {
      return EXPECTED_SIDE;
    }

    CSSValue secondLength = ZERO_PERCENT;
    if (!(values[i] instanceof BackgroundPositionSide)) {
      secondLength = values[i];
      i++;
    }

    if (i < values.length && values[i] != null) {
      return CSSFailure.EXPECTED_EOF;
    }

    if (
      firstSide.isHorizontal() == secondSide.isHorizontal()
      && !firstSide.equals(BackgroundPositionSide.CENTER)
    ) {
      return MIXED_SIDES;
    }

    return !firstSide.isHorizontal() || !secondSide.isVertical() ?
      BackgroundPositionValue.create(secondSide, secondLength, firstSide, firstLength) :
      BackgroundPositionValue.create(firstSide, firstLength, secondSide, secondLength);
  }

  private CSSValue decodeValue(CSSTokenStream stream) throws IOException {
    int mark = stream.mark();

    if (
      stream.peek() instanceof IdentToken identToken
      && BACKGROUND_POSITION_VALUES.containsKey(identToken.value())
    ) {
      stream.read();
      return BACKGROUND_POSITION_VALUES.get(identToken.value());
    }
    
    CSSValue sizeResult = sizeParser.parse(stream);
    if (sizeResult.isFailure()) {
      stream.restoreMark(mark);
      return null;
    }

    stream.discardMark();
    return sizeResult;
    
  }
  
}
