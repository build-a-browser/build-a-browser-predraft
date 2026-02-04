package net.buildabrowser.babbrowser.cssbase.tokenizer.imp;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizer;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LParenToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RParenToken;
import net.buildabrowser.babbrowser.cssbase.tokens.SemicolonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
import net.buildabrowser.babbrowser.cssbase.tokens.WhitespaceToken;

public class CSSTokenizerImp implements CSSTokenizer {

  private final IdentTokenizer identTokenizer = new IdentTokenizer();
  private final NumberTokenizer numberTokenizer = new NumberTokenizer(identTokenizer);

  @Override
  public Token consumeAToken(CSSTokenizerInput stream) throws IOException {
    consumeComments(stream);
    int ch = stream.read();
    // TODO: More cases
    return switch (ch) {
      case '\n', ' ', '\t' -> consumeWhitespace(stream);
      case '#' -> consumeNumberSign(stream);
      case '(' -> LParenToken.create();
      case ')' -> RParenToken.create();
      case '+' -> consumePlusSign(stream);
      case ',' -> CommaToken.create();
      case '-' -> consumeHyphenMinusSign(stream);
      case '.' -> consumeFullStop(stream);
      case ':' -> ColonToken.create();
      case ';' -> SemicolonToken.create();
      case '\\' -> consumeReverseSolidus(stream);
      case '{' -> LCBracketToken.create();
      case '}' -> RCBracketToken.create();
      case -1 -> EOFToken.create();
      default -> handleOtherValue(stream, ch);
    };
  }

  private Token handleOtherValue(CSSTokenizerInput stream, int ch) throws IOException {
    if (TokenizerUtil.isDigit(ch)) {
      stream.unread(ch);
      return numberTokenizer.consumeANumericToken(stream);
    } else if (TokenizerUtil.isIdentStartCodePoint(ch)) {
      stream.unread(ch);
      return identTokenizer.consumeAnIdentLikeToken(stream);
    }
    // TODO: Handle actual other values
    return DelimToken.create(ch);
  }


  private void consumeComments(CSSTokenizerInput stream) throws IOException {
    int ch1 = stream.read();
    int ch2 = stream.peek();
    if (ch1 == '/' && ch2 == '*') {
      stream.read();
      ch1 = stream.read();
      // TODO: Will this error if ch1 was -1?
      ch2 = stream.peek();
      while (ch1 != '*' && ch2 != '/') {
        if (ch2 == -1) {
          // TODO: Parse error
          return;
        }

        ch1 = stream.read();
        ch2 = stream.peek();
      }
      stream.read();
      return;
    }
    stream.unread(ch1);
  }

  private WhitespaceToken consumeWhitespace(CSSTokenizerInput stream) throws IOException {
    int ch;
    while ((ch = stream.read()) == '\n' || ch == ' ' || ch == '\t') {}
    stream.unread(ch);

    return WhitespaceToken.create();
  }

  private Token consumeNumberSign(CSSTokenizerInput stream) throws IOException {
    if (TokenizerUtil.isIdentCodePoint(stream.peek()) || TokenizerUtil.isValidEscape(stream)) {
      boolean isId = TokenizerUtil.wouldStartAnIdentSequence(stream);
      String seq = identTokenizer.consumeIdentSequence(stream);
      return HashToken.create(
        seq,
        isId ? HashToken.Type.ID : HashToken.Type.UNRESTRICTED);
    }

    return DelimToken.create(stream.read());
  }

  private Token consumePlusSign(CSSTokenizerInput stream) throws IOException {
    if (numberTokenizer.startsWithANumber('+', stream)) {
      stream.unread('+');
      return numberTokenizer.consumeANumericToken(stream);
    }

    return DelimToken.create('+');
  }

  private Token consumeHyphenMinusSign(CSSTokenizerInput stream) throws IOException {
    if (numberTokenizer.startsWithANumber('-', stream)) {
      stream.unread('-');
      return numberTokenizer.consumeANumericToken(stream);
    }
    // TODO: Handle CDC
    else if (TokenizerUtil.wouldStartAnIdentSequence(stream)) {
      stream.unread('-');
      return identTokenizer.consumeAnIdentLikeToken(stream);
    } else {
      return DelimToken.create('-');
    }
  }
  
  private Token consumeFullStop(CSSTokenizerInput stream) throws IOException {
    if (numberTokenizer.startsWithANumber('.', stream)) {
      stream.unread('.');
      return numberTokenizer.consumeANumericToken(stream);
    }

    return new DelimToken('.');
  }

  private Token consumeReverseSolidus(CSSTokenizerInput stream) throws IOException {
    if (TokenizerUtil.isValidEscape('\\', stream.peek())) {
      stream.unread('\\');
      return identTokenizer.consumeAnIdentLikeToken(stream);
    }

    // TODO: Parse error
    return new DelimToken('\\');
  }
  
}
