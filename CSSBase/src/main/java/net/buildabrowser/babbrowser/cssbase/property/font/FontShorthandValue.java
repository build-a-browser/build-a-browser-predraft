package net.buildabrowser.babbrowser.cssbase.property.font;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record FontShorthandValue(
  CSSValue fontWeight, CSSValue fontSize, CSSValue lineHeight, CSSValue fontFamily
) implements CSSValue {
  
}
