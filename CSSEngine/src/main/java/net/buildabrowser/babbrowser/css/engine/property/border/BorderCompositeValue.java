package net.buildabrowser.babbrowser.css.engine.property.border;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;

public record BorderCompositeValue(CSSValue width, CSSValue color, CSSValue style) implements CSSValue {
  
}
