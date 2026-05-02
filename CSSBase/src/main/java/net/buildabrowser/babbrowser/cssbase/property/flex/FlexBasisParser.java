package net.buildabrowser.babbrowser.cssbase.property.flex;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class FlexBasisParser implements PropertyValueParser {

  private final SizeParser sizeParser = SizeParser.forNormal(null);

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("content")
    ) {
      stream.read();
      return FlexBasisValue.CONTENT;
    }

    return sizeParser.parse(stream);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.FLEX_BASIS;
  }
  
}
