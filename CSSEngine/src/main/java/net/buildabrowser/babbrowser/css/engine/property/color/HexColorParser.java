package net.buildabrowser.babbrowser.css.engine.property.color;

import java.io.IOException;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken;

public class HexColorParser implements PropertyValueParser {

  private static final CSSFailure NO_HASH_FAILURE = new CSSFailure("Color does not have a hash");
  private static final CSSFailure WRONG_SIZE_FAILURE = new CSSFailure("Hash must be 3, 4, 6, or 8 characters");
  private static final CSSFailure COMPONENT_NOT_VALID_FAILURE = new CSSFailure("Component not valid");

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    if (!(stream.read() instanceof HashToken hashToken)) {
      return NO_HASH_FAILURE;   
    }

    String hexValue = hashToken.value();
    return switch (hexValue.length()) {
      case 3 -> parse3Components(hexValue);
      case 4 -> parse4Components(hexValue);
      case 6 -> parse6Components(hexValue);
      case 8 -> parse8Components(hexValue);
      default -> WRONG_SIZE_FAILURE;
    };
  }

  private CSSValue parse3Components(String hexValue) {
    int c1 = parseSingleComponent(hexValue, 0);
    int c2 = parseSingleComponent(hexValue, 1);
    int c3 = parseSingleComponent(hexValue, 2);

    if (c1 == -1 || c2 == -1 || c3 == -1) {
      return COMPONENT_NOT_VALID_FAILURE;
    }

    return SRGBAColor.create(c1, c2, c3, 255);
  }

  private CSSValue parse4Components(String hexValue) {
    int c1 = parseSingleComponent(hexValue, 0);
    int c2 = parseSingleComponent(hexValue, 1);
    int c3 = parseSingleComponent(hexValue, 2);
    int c4 = parseSingleComponent(hexValue, 3);

    if (c1 == -1 || c2 == -1 || c3 == -1 || c4 == -1) {
      return COMPONENT_NOT_VALID_FAILURE;
    }

    return SRGBAColor.create(c1, c2, c3, c4);
  }

  private CSSValue parse6Components(String hexValue) {
    int c1 = parseDoubleComponent(hexValue, 0);
    int c2 = parseDoubleComponent(hexValue, 2);
    int c3 = parseDoubleComponent(hexValue, 4);

    if (c1 == -1 || c2 == -1 || c3 == -1) {
      return COMPONENT_NOT_VALID_FAILURE;
    }

    return SRGBAColor.create(c1, c2, c3, 255);
  }

  private CSSValue parse8Components(String hexValue) {
    int c1 = parseDoubleComponent(hexValue, 0);
    int c2 = parseDoubleComponent(hexValue, 2);
    int c3 = parseDoubleComponent(hexValue, 4);
    int c4 = parseDoubleComponent(hexValue, 6);

    if (c1 == -1 || c2 == -1 || c3 == -1 || c4 == -1) {
      return COMPONENT_NOT_VALID_FAILURE;
    }

    return SRGBAColor.create(c1, c2, c3, c4);
  }

  private int parseSingleComponent(String hexValue, int index) {
    int hex = fromHex(hexValue.codePointAt(index));
    if (hex == -1) return -1;

    return hex * 16 + hex;
  }

  private int parseDoubleComponent(String hexValue, int index) {
    int hex1 = fromHex(hexValue.codePointAt(index));
    int hex2 = fromHex(hexValue.codePointAt(index + 1));
    if (hex1 == -1 || hex2 == -1) return -1;

    return hex1 * 16 + hex2;
  }

  // TODO: Move to ASCIIUtil
  private int fromHex(int ch) {
    if (ch >= '0' && ch <= '9') {
      return ch - '0';
    } else if (ch >= 'A' && ch <= 'F') {
      return ch - 'A' + 10;
    } else if (ch >= 'a' && ch <= 'f') {
      return ch - 'a' + 10;
    } else {
      return -1;
    }
  }

}
