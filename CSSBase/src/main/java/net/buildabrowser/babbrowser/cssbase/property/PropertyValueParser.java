package net.buildabrowser.babbrowser.cssbase.property;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;

public interface PropertyValueParser {
 
  CSSValue parse(CSSTokenStream stream) throws IOException;

  default CSSProperty relatedProperty() {
    return null;
  }

  default void updateProperty(CSSValue result, MutablePropertyContainer propertySetter) {
    propertySetter.setProperty(relatedProperty(), result);
  }

}
