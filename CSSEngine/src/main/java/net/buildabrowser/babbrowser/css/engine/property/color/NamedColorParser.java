package net.buildabrowser.babbrowser.css.engine.property.color;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class NamedColorParser implements PropertyValueParser {

  private static final CSSFailure INVALID_TOKEN = new CSSFailure("Expected an ident token");
  private static final CSSFailure UNKNOWN_COLOR = new CSSFailure("Not a known color!");

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    Token token = stream.read();
    if (!(token instanceof IdentToken identToken)) {
      return INVALID_TOKEN;
    }

    SRGBAColor color = HTMLColors.HTML_COLORS.get(identToken.value());
    return color == null ? UNKNOWN_COLOR : color;
  }
  
}
