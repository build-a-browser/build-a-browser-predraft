package net.buildabrowser.babbrowser.css.engine.property.border;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.css.engine.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class BorderSizeParser implements PropertyValueParser {

  private final SizeParser innerParser;

  public BorderSizeParser(CSSProperty property) {
    this.innerParser = new SizeParser(false, false, false, property);
  }

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    if (stream.peek() instanceof IdentToken identToken) {
      CSSValue value = switch (identToken.value()) {
        // More convenient than a dedicated border type
        // Adjust these to preference, it's UA-dependent
        case "thin" -> LengthValue.create(2, true, LengthType.PX);
        case "medium" -> LengthValue.create(4, true, LengthType.PX);
        case "thick" -> LengthValue.create(8, true, LengthType.PX);
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
