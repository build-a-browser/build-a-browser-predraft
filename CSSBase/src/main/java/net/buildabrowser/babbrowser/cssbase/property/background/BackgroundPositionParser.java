package net.buildabrowser.babbrowser.cssbase.property.background;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundPositionValue.BackgroundPositionSide;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;

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

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseCommaRepeat(stream, this::parseInternal);
  }

  public CSSValue parseInternal(SeekableCSSTokenStream stream) throws IOException {
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
    } else if (values[0] instanceof PercentageValue percentageValue) {
      return BackgroundPositionValue.create(
          BackgroundPositionSide.LEFT, percentageValue,
          BackgroundPositionSide.CENTER, ZERO_PERCENT);
    } else {
      return EXPECTED_SIDE;
    }
  }

  private CSSValue parseDoubleValue(CSSValue[] values) {
    boolean hasPercent = false;
    BackgroundPositionSide horizontalSide = BackgroundPositionSide.LEFT;
    PercentageValue horizontalPercentage = ZERO_PERCENT;
    if (values[0] instanceof BackgroundPositionSide side) {
      horizontalSide = side;
    } else {
      hasPercent = true;
      horizontalPercentage = (PercentageValue) values[0];
    }

    BackgroundPositionSide verticalSide = BackgroundPositionSide.TOP;
    PercentageValue verticalPercentage = ZERO_PERCENT;
    if (values[1] instanceof BackgroundPositionSide side) {
      verticalSide = side;
    } else {
      hasPercent = true;
      verticalPercentage = (PercentageValue) values[1];
    }

    if (hasPercent) {
      if (!(horizontalSide.isHorizontal() && verticalSide.isVertical())) {
        return MIXED_SIDES;
      }
      return BackgroundPositionValue.create(horizontalSide, horizontalPercentage, verticalSide, verticalPercentage);
    } else {
      if (
        !(horizontalSide.isHorizontal() && verticalSide.isVertical())
        && !(
          horizontalSide.equals(BackgroundPositionSide.CENTER) ||
          verticalSide.equals(BackgroundPositionSide.CENTER))
      ) {
        return MIXED_SIDES;
      }

      return !horizontalSide.isHorizontal() || !verticalSide.isVertical() ?
        BackgroundPositionValue.create(verticalSide, verticalPercentage, horizontalSide, horizontalPercentage) :
        BackgroundPositionValue.create(horizontalSide, horizontalPercentage, verticalSide, verticalPercentage);
    }  
  }

  private CSSValue parse34Value(CSSValue[] values) {
    int i = 0;
    if (!(values[i++] instanceof BackgroundPositionSide firstSide)) {
      return EXPECTED_SIDE;
    }

    PercentageValue firstPercentage = ZERO_PERCENT;
    if (values[i] instanceof PercentageValue percentageValue) {
      i++;
      firstPercentage = percentageValue;
    }

    if (!(values[i++] instanceof BackgroundPositionSide secondSide)) {
      return EXPECTED_SIDE;
    }

    PercentageValue secondPercentage = ZERO_PERCENT;
    if (values[i] instanceof PercentageValue percentageValue) {
      i++;
      secondPercentage = percentageValue;
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
      BackgroundPositionValue.create(secondSide, secondPercentage, firstSide, firstPercentage) :
      BackgroundPositionValue.create(firstSide, firstPercentage, secondSide, secondPercentage);
  }

  private CSSValue decodeValue(SeekableCSSTokenStream stream) throws IOException {
    return switch (stream.peek()) {
      case PercentageToken percent -> {
        stream.read();
        yield PercentageValue.create(percent.value());
      }
      case IdentToken identToken -> {
        CSSValue decodedValue = BACKGROUND_POSITION_VALUES.get(identToken.value());
        if (decodedValue != null) stream.read();
        yield decodedValue;
      }
      default -> null;
    };
  }
  
}
