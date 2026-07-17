package net.buildabrowser.babbrowser.cssbase.property.size;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public enum SizeValue implements CSSValue {
  MIN_CONTENT, MAX_CONTENT,
  STRETCH, FIT_CONTENT, CONTAIN;

  // TODO: Also support the Level 4 fit-content(stretch)
  public static record FitContent(CSSValue optimal) implements CSSValue {

    public static FitContent create(CSSValue optimal) {
      return new FitContent(optimal);
    }

  }
}
