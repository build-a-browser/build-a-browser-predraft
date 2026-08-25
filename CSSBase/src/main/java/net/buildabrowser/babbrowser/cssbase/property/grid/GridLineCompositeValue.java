package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.util.List;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record GridLineCompositeValue(
  List<CSSValue> gridLines
) implements CSSValue {
  
  @Override
  public String serialize() {
    return "<UNIMPLEMENTED>";
  }

}
