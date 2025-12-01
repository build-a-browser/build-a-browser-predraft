package net.buildabrowser.babbrowser.css.engine.property;

public interface CSSValue {

  public static CSSValue INHERIT = new SpecialCSSValue(SpecialCSSType.INHERIT);
  public static CSSValue AUTO = new SpecialCSSValue(SpecialCSSType.AUTO);
  public static CSSValue NONE = new SpecialCSSValue(SpecialCSSType.NONE);
  
  default boolean isFailure() {
    return false;
  };

  default boolean isSpecial(SpecialCSSType type) {
    return false;
  }

  public static record CSSFailure(String reason) implements CSSValue {

    public boolean isFailure() {
      return true;
    }

  }

  public static record CSSSuccess() implements CSSValue {}

  public static record SpecialCSSValue(SpecialCSSType type) implements CSSValue {

    public boolean isSpecial(SpecialCSSType type) {
      return this.type.equals(type);
    }

  }

  static enum SpecialCSSType {
    INHERIT, AUTO, NONE
  }

}
