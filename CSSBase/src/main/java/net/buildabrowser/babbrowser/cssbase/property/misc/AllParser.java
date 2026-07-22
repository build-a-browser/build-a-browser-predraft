package net.buildabrowser.babbrowser.cssbase.property.misc;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;

public class AllParser implements PropertyValueParser {

  private static final CSSFailure ALL_FAILURE = new CSSFailure(
    "Intentional failure to trigger fallback for property 'all'.");

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return ALL_FAILURE;
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.ALL;
  }
  
}
