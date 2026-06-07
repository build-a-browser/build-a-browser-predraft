package net.buildabrowser.babbrowser.renderer.hintattr;

import java.util.Set;

import net.buildabrowser.babbrowser.cssbase.property.text.TextAlignValue;
import net.buildabrowser.babbrowser.renderer.hintattr.PresentationalHint.PresentationalHintName;

public final class AlignAttributeResolver {
  
  private static final Set<String> ALLOWED_LEFT_ELEMENTS = Set.of(
    "div", "thead", "tbody", "tfoot", "tr", "td", "th");
  private static final Set<String> ALLOWED_CENTER_ELEMENTS = Set.of(
    "center", "div", "thead", "tbody", "tfoot", "tr", "td", "th");
  private static final Set<String> ALLOWED_RIGHT_ELEMENTS = Set.of(
    "div", "thead", "tbody", "tfoot", "tr", "td", "th");
  private static final Set<String> ALLOWED_JUSTIFY_ELEMENTS = Set.of(
    "div", "thead", "tbody", "tfoot", "tr", "td", "th");
  // TODO: Also valign embed, iframe, img, object, and image input

  private AlignAttributeResolver() {}

  public static PresentationalHint resolveAlignAttribute(
    String elName, PresentationalHintName name, String value
  ) {
    TextAlignValue alignValue = resolveAlignValue(elName, value);
    if (alignValue == null) return null;
    return PresentationalHintResolver.createLegacyAttribute(
      name, "text-align", alignValue);
  }

  public static TextAlignValue resolveAlignValue(String elName, String value) {
    if (value == null) return null;
    return switch (value.toLowerCase()) {
      case "left" -> ALLOWED_LEFT_ELEMENTS.contains(elName) ? TextAlignValue._BAB_LEFT : null;
      case "center", "middle" -> ALLOWED_CENTER_ELEMENTS.contains(elName) ? TextAlignValue._BAB_CENTER : null;
      case "right" -> ALLOWED_RIGHT_ELEMENTS.contains(elName) ? TextAlignValue._BAB_RIGHT : null;
      case "justify" -> ALLOWED_JUSTIFY_ELEMENTS.contains(elName) ? TextAlignValue._BAB_JUSTIFY : null;
      default -> null;
    };
  }

}
