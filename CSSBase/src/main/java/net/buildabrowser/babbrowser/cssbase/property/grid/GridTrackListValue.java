package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record GridTrackListValue(
  List<CSSValue> tracks, CSSValue repeat
) implements CSSValue {

  public static GridTrackListValue create(
    List<CSSValue> tracks, CSSValue repeat
  ) {
    return new GridTrackListValue(tracks, repeat);
  }
  
}
