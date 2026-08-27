package net.buildabrowser.babbrowser.cssbase.property;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStreamSource;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public interface CSSValue extends CSSValueOrFeature {

  public static CSSValue INHERIT = SpecialCSSValue.INHERIT;
  public static CSSValue AUTO = SpecialCSSValue.AUTO;
  public static CSSValue NONE = SpecialCSSValue.NONE;

  String serialize();
  
  default boolean isFailure() {
    return false;
  };

  default boolean isSpecial() {
    return false;
  }

  public static record CSSFailure(String reason) implements CSSValue {

    public static final CSSFailure EXPECTED_EOF = new CSSFailure("Expected an EOF token!");
    public static final CSSFailure EXPECTED_COMMA = new CSSFailure("Expected a comma token!");
    public static final CSSFailure EXPECTED_INTEGER = new CSSFailure("Expected an integer value!");
    public static final CSSFailure EXPECTED_NUMBER = new CSSFailure("Expected a numerical value!");
    public static final CSSFailure EXPECTED_POSITIVE_NUMBER = new CSSFailure("Expected a positive numerical value!");
    public static final CSSFailure EXPECTED_PERCENTAGE = new CSSFailure("Expected a percentage token!");
    public static final CSSFailure EXPECTED_STRING = new CSSFailure("Expected a string token!");
    public static final CSSFailure EXPECTED_IDENT = new CSSFailure("Expected an identifier token!");

    public static final CSSFailure UNSET_CUSTOM_PROPERTY = new CSSFailure("Custom property is unset");

    public boolean isFailure() {
      return true;
    }

    @Override
    public String serialize() {
      return String.join("",
      "[Failure: ", reason, "]");
    }

  }

  public static record CSSDeferred(
    Declaration value,
    PropertyValueParser parser,
    List<String> varReferences,
    CSSTokenStreamSource source
  ) implements CSSValue {

    @Override
    public String serialize() {
      return CSSSerializerUtil.serializeTokenList(value.value());
    }
  
  }

  public static record CSSDeferredWithFallback(
    CSSDeferred inner,
    CSSValue fallback
  ) implements CSSValue {

    @Override
    public String serialize() {
      return inner.serialize();
    }
  
  }

  public static record CSSVarValue(
    List<Token> propertyTokens
  ) implements CSSValue {

    @Override
    public String serialize() {
      return CSSSerializerUtil.serializeTokenList(propertyTokens);
    }

  }

  static enum SpecialCSSValue implements CSSValue {
    INHERIT, AUTO, NONE,
    INVALID, INITIAL, UNSET;

    @Override
    public String serialize() {
      return this.name().toLowerCase();
    }

    @Override
    public boolean isSpecial() {
      return true;
    }
  }

}
