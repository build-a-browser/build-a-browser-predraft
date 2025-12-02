package net.buildabrowser.babbrowser.cssbase.tokenizer.imp;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

import static net.buildabrowser.babbrowser.cssbase.tokenizer.imp.TokenizerUtil.isDigit;

public class NumberTokenizer {

  private final IdentTokenizer identTokenizer;

  public NumberTokenizer(IdentTokenizer identTokenizer) {
    this.identTokenizer = identTokenizer;
  }

  public Token consumeANumericToken(CSSTokenizerInput stream) throws IOException {
    NumberToken number = consumeANumber(stream);
    if (TokenizerUtil.wouldStartAnIdentSequence(stream)) {
      String value = identTokenizer.consumeIdentSequence(stream);
      return DimensionToken.create(number.value(), number.isInteger(), value);
    } else if (stream.peek() == '%') {
      stream.read();
      return PercentageToken.create(number.value());
    } else {
      return number;
    }
  }
  
  private NumberToken consumeANumber(CSSTokenizerInput stream) throws IOException {
    boolean isInteger = true;
    StringBuilder repr = new StringBuilder();
    
    int ch = stream.peek();
    if (ch == '+' || ch == '-') {
      repr.appendCodePoint(stream.read());
    }

    while (isDigit(stream.peek())) {
      repr.appendCodePoint(stream.read());
    }

    ch = stream.read();
    if (ch == '.' && isDigit(stream.peek())) {
      repr.appendCodePoint(ch);
      repr.appendCodePoint(stream.read());
      isInteger = false;
      while (isDigit(stream.peek())) {
        repr.appendCodePoint(stream.read());
      }
    } else {
      stream.unread(ch);
    }

    int ch1 = stream.read();
    int ch2 = stream.read();
    int ch3 = stream.peek();
    stream.unread(ch2);
    stream.unread(ch1);

    if (
      (ch1 == 'E' || ch1 == 'e')
      && (
        ((ch2 == '-' || ch2 == '+') && isDigit(ch3))
        || isDigit(ch2)
      )
    ) {
      repr.appendCodePoint(stream.read());
      repr.appendCodePoint(stream.read());
      if (ch2 == '-' || ch2 == '+') {
        repr.appendCodePoint(stream.read());
      }
    }

    Number value = isInteger ?
      Integer.valueOf(repr.toString()) :
      Double.valueOf(repr.toString());

    return NumberToken.create(value, isInteger);
  }

  public boolean startsWithANumber(int ch1, int ch2, int ch3) {
    switch (ch1) {
      case '+', '-':
        if (isDigit(ch2)) return true;
        if (ch2 == '.' && isDigit(ch3)) return true;
        return false;
      case '.':
        return isDigit(ch2);
      default:
        return isDigit(ch1);
    }
  }

  public boolean startsWithANumber(int ch1, CSSTokenizerInput stream) throws IOException {
    int ch2 = stream.read();
    int ch3 = stream.peek();
    stream.unread(ch2);

    return startsWithANumber(ch1, ch2, ch3);
  }

}
