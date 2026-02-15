package net.buildabrowser.babbrowser.cssbase.property;

public interface CSSValue {

  public static CSSValue INHERIT = SpecialCSSValue.INHERIT;
  public static CSSValue AUTO = SpecialCSSValue.AUTO;
  public static CSSValue NONE = SpecialCSSValue.NONE;
  
  default boolean isFailure() {
    return false;
  };

  default boolean isSpecial() {
    return false;
  }

  public static record CSSFailure(String reason) implements CSSValue {

    public static CSSFailure EXPECTED_EOF = new CSSFailure("Expected an EOF token!");
    public static CSSFailure EXPECTED_INTEGER = new CSSFailure("Expected an integer value!");
    public static CSSFailure EXPECTED_NUMBER = new CSSFailure("Expected a numerical value!");
    public static CSSFailure EXPECTED_POSITIVE_NUMBER = new CSSFailure("Expected a positive numerical value!");

    public boolean isFailure() {
      return true;
    }

  }

  public static record CSSSuccess() implements CSSValue {}

  static enum SpecialCSSValue implements CSSValue {
    INHERIT, AUTO, NONE,
    INVALID, INITIAL, UNSET;

    @Override
    public boolean isSpecial() {
      return true;
    }
  }

}
