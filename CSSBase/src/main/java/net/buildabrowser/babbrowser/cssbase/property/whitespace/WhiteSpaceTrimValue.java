package net.buildabrowser.babbrowser.cssbase.property.whitespace;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record WhiteSpaceTrimValue(
  boolean discardBefore,
  boolean disacardAfter,
  boolean discardInner
) implements CSSValue {

  public static WhiteSpaceTrimValue NONE = new WhiteSpaceTrimValue(
    false, false, false);

  public static enum WhiteSpaceTrimComponent implements CSSValue {
    DISCARD_BEFORE, DISCARD_AFTER, DISCARD_INNER;
  }

}
