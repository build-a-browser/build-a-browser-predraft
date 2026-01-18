package net.buildabrowser.babbrowser.css.engine.property.color;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class ColorBaseParser implements PropertyValueParser {

  private final PropertyValueParser hexColorParser = new HexColorParser();
  private final PropertyValueParser namedColorParser = new NamedColorParser();
  private final PropertyValueParser rgbColorParser = new RGBColorParser();

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream, ActiveStyles activeStyles) throws IOException {
    Token nextToken = stream.read();
    if (
      nextToken instanceof IdentToken identToken
      && identToken.value().equals("transparent")
    ) {
      return SRGBAColor.create(0, 0, 0, 0);
    }
    stream.unread(nextToken);

    return PropertyValueParserUtil.parseLongest(stream,
      hexColorParser,
      namedColorParser,
      rgbColorParser
    );
  }
  
}
