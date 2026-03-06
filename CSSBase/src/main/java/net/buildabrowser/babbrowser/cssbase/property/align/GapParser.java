package net.buildabrowser.babbrowser.cssbase.property.align;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class GapParser implements PropertyValueParser {

  private final SizeParser lineSizeParser = new SizeParser(false, false, null);
  
  private final CSSProperty relatedProperty;

  public GapParser(CSSProperty relatedProperty) {
    this.relatedProperty = relatedProperty;
  }

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("normal")
    ) {
      stream.read();
      return GapValue.NORMAL;
    } else {
      return lineSizeParser.parse(stream);
    }
  }

  @Override
  public CSSProperty relatedProperty() {
    return this.relatedProperty;
  }

}
