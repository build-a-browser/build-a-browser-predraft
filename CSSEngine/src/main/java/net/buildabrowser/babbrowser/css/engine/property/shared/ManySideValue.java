package net.buildabrowser.babbrowser.css.engine.property.shared;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

public record ManySideValue(CSSValue top, CSSValue right, CSSValue bottom, CSSValue left) implements CSSValue {
  
}
