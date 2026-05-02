package net.buildabrowser.babbrowser.cssbase.property.text;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class LineHeightParser implements PropertyValueParser {

  private final SizeParser lineSizeParser = new SizeParser(false, false, null);

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    Token token = stream.peek();
    if (
      token instanceof IdentToken identToken
      && identToken.value().equals("normal")
    ) {
      stream.read();
      return LineHeightValue.NORMAL;
    } else if (
      token instanceof NumberToken numberToken
      && numberToken.value().floatValue() > 0
    ) {
      stream.read();
      return LineHeightValue.NumberHeight.create(numberToken.value());
    } else {
      return lineSizeParser.parse(stream);
    }
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.LINE_HEIGHT;
  }
  
}
