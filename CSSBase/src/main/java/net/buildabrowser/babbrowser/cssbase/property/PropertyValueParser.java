package net.buildabrowser.babbrowser.cssbase.property;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;

public interface PropertyValueParser {
 
  CSSValue parse(SeekableCSSTokenStream stream) throws IOException;

  default CSSProperty relatedProperty() {
    return null;
  }

  default void updateProperty(CSSValue result, PropertyContainer propertySetter) {
    propertySetter.setProperty(relatedProperty(), result);
  }

}
