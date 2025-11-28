package net.buildabrowser.babbrowser.css.engine.property;

public interface CSSValue {

  public static CSSSuccess SUCCESS = new CSSSuccess();
  
  default boolean isFailure() {
    return false;
  };

  public static record CSSFailure(String reason) implements CSSValue {

    public boolean isFailure() {
      return true;
    }

  }

  public static record CSSSuccess() implements CSSValue {}

}
