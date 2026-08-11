package net.buildabrowser.babbrowser.cssbase.property;

import java.util.List;
import java.util.Objects;

import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public final class CSSSerializerUtil {

  private CSSSerializerUtil() {}
  
  public static String serialize(Number number) {
    if (number.intValue() == number.floatValue()) {
      return String.valueOf(number.intValue());
    }

    return number.toString();
  }

  public static String serializeEnum(Object o) {
    return ((Enum<?>) o).name().toLowerCase().replace('_', '-');
  }

  public static String serializeManySpaces(CSSValue... items) {
    StringBuilder serialBuilder = new StringBuilder();
    for (CSSValue item: items) {
      if (item == null) continue;
      if (!serialBuilder.isEmpty()) {
        serialBuilder.append(' ');
      }
      serialBuilder.append(item.serialize());
    }

    return serialBuilder.toString();
  }

  public static String serializeManySpaces(List<CSSValue> items) {
    return serializeManySpaces(items.toArray(CSSValue[]::new));
  }

  public static String serializeManyCommas(CSSValue... items) {
    StringBuilder serialBuilder = new StringBuilder();
    for (CSSValue item: items) {
      if (item == null) continue;
      if (!serialBuilder.isEmpty()) {
        serialBuilder.append(' ');
      }
      serialBuilder.append(item.serialize());
    }

    return serialBuilder.toString();
  }

  public static String serializeValue(CSSValue value) {
    if (value == null) return "";
    return value.serialize();
  }

  public static String serializeString(String value) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'serializeString'");
  }

  public static String serializeMaybeEqual(CSSValue a, CSSValue b) {
    if (Objects.equals(a, b)) {
      return CSSSerializerUtil.serializeValue(a);
    } else {
      return CSSSerializerUtil.serializeManySpaces(a, b);
    }
  }

  public static String formatFunction(String name, CSSValue... args) {
    return String.join("",
      name, "(", serializeManyCommas(args), ")");
  }

  // TODO: Implement token list serialization
  public static String serializeTokenList(List<Token> value) {
    return "/*<UNIMPLEMENTED>*/";
  }

}
