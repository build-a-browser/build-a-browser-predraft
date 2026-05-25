package net.buildabrowser.babbrowser.cssbase.property.table;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public record BorderSpacingValue(
  CSSValue hSpace, CSSValue vSpace
) implements CSSValue {

  public static BorderSpacingValue create(
    CSSValue hSpace, CSSValue vSpace
  ) {
    return new BorderSpacingValue(hSpace, vSpace);
  }

}
