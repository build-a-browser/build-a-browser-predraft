package net.buildabrowser.babbrowser.cssbase.property.shared;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class LineWidthParser implements PropertyValueParser {

  private final SizeParser innerParser;

  public LineWidthParser(CSSProperty property) {
    this.innerParser = new SizeParser(false, false, false, false, property);
  }

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    if (stream.peek() instanceof IdentToken identToken) {
      CSSValue value = switch (identToken.value()) {
        // More convenient than a dedicated border type
        // Adjust these to preference, it's UA-dependent
        case "thin" -> LengthValue.THIN;
        case "medium" -> LengthValue.MEDIUM;
        case "thick" -> LengthValue.THICK;
        default -> null;
      };
      if (value != null) {
        stream.read();
        return value;
      }
    }

    return innerParser.parse(stream);
  }

  @Override
  public CSSProperty relatedProperty() {
    return innerParser.relatedProperty();
  }
  
}
