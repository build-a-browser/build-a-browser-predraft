package net.buildabrowser.babbrowser.cssbase.property.whitespace;

import java.util.StringJoiner;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record WhiteSpaceTrimValue(
  boolean discardBefore,
  boolean discardAfter,
  boolean discardInner
) implements CSSValue {

  public static WhiteSpaceTrimValue NONE = new WhiteSpaceTrimValue(
    false, false, false);

  public static enum WhiteSpaceTrimComponent implements CSSValue {
    DISCARD_BEFORE, DISCARD_AFTER, DISCARD_INNER;

    @Override
    public String serialize() {
      return CSSSerializerUtil.serializeEnum(this);
    }
  }

  @Override
  public String serialize() {
    if (!(discardBefore || discardAfter || discardInner)) {
      return "none";
    }

    StringJoiner values = new StringJoiner(" ");
    if (discardBefore) {
      values.add("discard-before");
    }
    if (discardAfter) {
      values.add("discard-after");
    }
    if (discardInner) {
      values.add("discard-inner");
    }

    return values.toString();
  }

}
