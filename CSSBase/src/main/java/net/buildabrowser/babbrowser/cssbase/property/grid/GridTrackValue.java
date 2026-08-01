package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record GridTrackValue(
  List<String> lineNames, CSSValue sizeOrRepeat
) implements CSSValue {

  public static record GridRepeatValue(
    CSSValue repeatTimesValue,
    CSSValue tracks
  ) implements CSSValue {

    public static CSSValue create(
      CSSValue repeatTimesValue, CSSValue trackList
    ) {
      return new GridRepeatValue(repeatTimesValue, trackList);
    }

  }
  
  public static record GridRepeatNumberComponent(int numRepeats) implements CSSValue {

    public static GridRepeatNumberComponent create(int numRepeats) {
      return new GridRepeatNumberComponent(numRepeats);
    }

  }

  public static enum GridRepeatNameComponent implements CSSValue {
    AUTO_FILL, AUTO_FIT
  }

  public static GridTrackValue create(
    List<String> lineNames, CSSValue sizeOrRepeat
  ) {
    return new GridTrackValue(lineNames, sizeOrRepeat);
  }

}
