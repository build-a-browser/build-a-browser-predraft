package net.buildabrowser.babbrowser.cssbase.property.shared;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record ManySideValue(CSSValue top, CSSValue right, CSSValue bottom, CSSValue left) implements CSSValue {
  
}
