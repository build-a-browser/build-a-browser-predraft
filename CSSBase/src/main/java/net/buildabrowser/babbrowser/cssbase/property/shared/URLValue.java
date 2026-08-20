package net.buildabrowser.babbrowser.cssbase.property.shared;

import java.net.URI;

import net.buildabrowser.babbrowser.cssbase.property.CSSSerializerUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

// Added URI field not in spec, to track CSS stylesheet source
public record URLValue(String value, URI refURL) implements CSSValue {

  @Override
  public String serialize() {
    return String.join("",
      "url(",
      CSSSerializerUtil.serializeString(value),
      ")");
  }
  
  public static URLValue create(String value, URI refURL) {
    return new URLValue(value, refURL);
  }

}
