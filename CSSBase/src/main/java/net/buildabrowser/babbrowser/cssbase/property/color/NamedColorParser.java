package net.buildabrowser.babbrowser.cssbase.property.color;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class NamedColorParser implements PropertyValueParser {

  private static final CSSFailure INVALID_TOKEN = new CSSFailure("Expected an ident token");
  private static final CSSFailure UNKNOWN_COLOR = new CSSFailure("Not a known color!");

  // Another place where NAMED_COLORS is unfortunately a passed in singleton
  private static Map<String, ColorValue> NAMED_COLORS;

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    if (NAMED_COLORS == null) {
      throw new IllegalStateException("NAMED_COLORS is a singleton that must be initialized!");
    }

    Token token = stream.read();
    if (!(token instanceof IdentToken identToken)) {
      return INVALID_TOKEN;
    }

    ColorValue color = NAMED_COLORS.get(identToken.value());
    return color == null ? UNKNOWN_COLOR : color;
  }

  public static void setNamedColors(Map<String, ColorValue> namedColors) {
    NAMED_COLORS = namedColors;
  }
  
}
