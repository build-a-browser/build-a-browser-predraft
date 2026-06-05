package net.buildabrowser.babbrowser.renderer.hintattr;

import java.util.Set;

import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.property.size.PercentageValue;
import net.buildabrowser.babbrowser.html.attrparse.DimensionParser;
import net.buildabrowser.babbrowser.html.attrparse.DimensionParser.DimensionParserResult;
import net.buildabrowser.babbrowser.renderer.hintattr.PresentationalHint.PresentationalHintName;

public final class WidthAttributeResolver {
  
  // TODO: There are other elements that are conditionally allowed (15.4.3)
  // TODO: Also need to map image aspect ratio
  private static final Set<String> ALLOWED_ELEMENTS = Set.of(
    "img", "col");
  private static final Set<String> ALLOWED_ELEMENTS_NONZERO = Set.of(
    "table", "td", "th");
  
  private WidthAttributeResolver() {}

  public static PresentationalHint resolveWidthAttribute(
    String elName, PresentationalHintName name, String value
  ) {
    boolean isNonZero = ALLOWED_ELEMENTS_NONZERO.contains(elName);
    if (!(ALLOWED_ELEMENTS.contains(elName) || isNonZero)) return null;

    DimensionParserResult dimension = DimensionParser.parseDimension(value);
    if (dimension == null) return null;
    if (isNonZero && dimension.number() == 0) return null;
    CSSValue retValue = dimension.isPercent() ?
      PercentageValue.create(dimension.number()) :
      LengthValue.create(dimension.number(), false, LengthType.PX);
    return PresentationalHintResolver.createLegacyAttribute(
      name, "width", retValue);
  }

}
