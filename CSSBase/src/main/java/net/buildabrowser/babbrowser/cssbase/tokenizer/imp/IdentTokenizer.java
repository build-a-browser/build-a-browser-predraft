package net.buildabrowser.babbrowser.cssbase.tokenizer.imp;

import static net.buildabrowser.babbrowser.common.util.ASCIIUtil.hexValue;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.cssbase.tokens.FunctionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class IdentTokenizer {

  private final StringBuilder strBuilder = new StringBuilder();

  public Token consumeAnIdentLikeToken(CSSTokenizerInput stream) throws IOException {
    String result = consumeIdentSequence(stream);

    // TODO: Handle URL
    if (stream.peek() == '(') {
      stream.read();
      return FunctionToken.create(result);
    }

    return IdentToken.create(result);
  }

  public String consumeIdentSequence(CSSTokenizerInput stream) throws IOException {
    strBuilder.setLength(0);

    while (
      TokenizerUtil.isValidEscape(stream) ||
      TokenizerUtil.isIdentCodePoint(stream.peek())
    ) {
      if (TokenizerUtil.isValidEscape(stream)) {
        stream.read();
        strBuilder.appendCodePoint(consumeAnEscapedCodepoint(stream));
      } else {
        strBuilder.appendCodePoint(stream.read());
      }
    }

    return strBuilder.toString();
  }

  private int consumeAnEscapedCodepoint(CSSTokenizerInput stream) throws IOException {
    int ch = stream.read();
    if (hexValue(ch) != -1) {
      int wholeValue = hexValue(ch);
      for (int i = 0; i < 5; i++) {
        if (hexValue(stream.peek()) != -1) {
          wholeValue = wholeValue * 16 + hexValue(stream.read());
        } else break;
      }

      if (
        wholeValue == 0
        // Character.isSurrogate does not accept an int
        || (ch >= 0xD800 && ch <= 0xDBFF)
        || (ch >= 0xDC00 && ch <= 0xDFFF)
        || ch >= 0x10FFFF
      ) return 0xFFFD;

      return wholeValue;
    } else if (ch == -1) {
      // TODO: Parse error
      return 0xFFFD;
    } else {
      return ch;
    }
  }

}
