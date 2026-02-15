package net.buildabrowser.babbrowser.cssbase.property;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public interface PropertyValueParser {
 
  CSSValue parse(SeekableCSSTokenStream stream) throws IOException;

  default CSSProperty relatedProperty() {
    return null;
  }

  default void updateProperty(CSSValue result, PropertySetter propertySetter) {
    propertySetter.setProperty(relatedProperty(), result);
  }

  interface PropertySetter {
    void setProperty(CSSProperty property, CSSValue value);
  }

}
