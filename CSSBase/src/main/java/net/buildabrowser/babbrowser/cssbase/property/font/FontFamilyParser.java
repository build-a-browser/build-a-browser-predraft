package net.buildabrowser.babbrowser.cssbase.property.font;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.StringToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class FontFamilyParser implements PropertyValueParser {

  // TODO: Support others
  private final Map<String, FontFamilyValue> FONT_FAMILIES = Map.of(
    "serif", FontFamilyValue.SERIF,
    "sans-serif", FontFamilyValue.SANS_SERIF,
    "monospace", FontFamilyValue.MONOSPACE
  );

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseCommaRepeat(stream, this::parseInner);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.FONT_FAMILY;
  }

  private CSSValue parseInner(CSSTokenStream stream) throws IOException {
    Token token = stream.read();
    if (token instanceof StringToken stringToken) {
      return FontNameValue.create(stringToken.value());
    } else if (
      token instanceof IdentToken identToken
      && FONT_FAMILIES.containsKey(identToken.value())
    ) {
      return FONT_FAMILIES.get(identToken.value());
    } else if (
      token instanceof IdentToken identToken
    ) {
      return FontNameValue.create(identToken.value());
    } else {
      return CSSFailure.EXPECTED_IDENT;
    }
  }
  
}
