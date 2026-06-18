package net.buildabrowser.babbrowser.cssbase.property.outline;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderColorParser;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class OutlineColorParser implements PropertyValueParser {

  private BorderColorParser borderColorParser = new BorderColorParser(null);

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("auto")
    ) {
      return CSSValue.AUTO;
    }

    return borderColorParser.parse(stream);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.OUTLINE_COLOR;
  }
  
}
