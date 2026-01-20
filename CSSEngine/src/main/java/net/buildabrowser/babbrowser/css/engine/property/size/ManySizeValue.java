package net.buildabrowser.babbrowser.css.engine.property.size;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

public record ManySizeValue(CSSValue top, CSSValue right, CSSValue bottom, CSSValue left) implements CSSValue {
  
}
