package net.buildabrowser.babbrowser.a11y.core.aom;

import java.util.HashMap;
import java.util.Map;

public enum AriaProperty {

  ACTIVEDESCENDANT, ATOMIC, AUTOCOMPLETE, BRAILLELABEL,
  BRAILLEROLEDESCRIPTION, BUSY, CHECKED, COLCOUNT, COLINDEX, COLINDEXTEXT,
  COLSPAN, CONTROLS, CURRENT, DESCRIBEDBY, DESCRIPTION, DETAILS, DISABLED,
  DROPEFFECT, ERRORMESSAGE, EXPANDED, FLOWTO, GRABBED, HASPOPUP, HIDDEN,
  INVALID, KEYSHORTCUTS, LABEL, LABELLEDBY, LEVEL, LIVE, MODAL, MULTILINE,
  MULTISELECTABLE, ORIENTATION, OWNS, PLACEHOLDER, POSINSET, PRESSED,
  READONLY, RELEVANT, REQUIRED, ROLEDESCRIPTION, ROWCOUNT, ROWINDEX,
  ROWINDEXTEXT, ROWSPAN, SELECTED, SETSIZE, SORT, VALUEMAX, VALUEMIN,
  VALUENOW, VALUETEXT;

  private static final Map<String, AriaProperty> PROPERTY_MAP;

  static {
    Map<String, AriaProperty> propertyMap = new HashMap<>();
    for (AriaProperty property: values()) {
      propertyMap.put(property.propertyName(), property);
    }

    PROPERTY_MAP = Map.copyOf(propertyMap);
  }

  public String propertyName() {
    return "aria-" + name().toLowerCase();
  }

  public static AriaProperty lookup(String name) {
    return PROPERTY_MAP.get(name);
  }

}
