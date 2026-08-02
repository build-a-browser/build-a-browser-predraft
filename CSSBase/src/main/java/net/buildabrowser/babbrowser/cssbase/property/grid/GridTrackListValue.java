package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record GridTrackListValue(
  List<GridTrackValue> tracks, CSSValue repeat
) implements CSSValue {

  public static GridTrackListValue create(
    List<GridTrackValue> tracks, CSSValue repeat
  ) {
    return new GridTrackListValue(tracks, repeat);
  }
  
}
