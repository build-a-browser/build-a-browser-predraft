package net.buildabrowser.babbrowser.cssbase.tokenizer;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.tokens.AtKeywordToken;
import net.buildabrowser.babbrowser.cssbase.tokens.BadStringToken;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.FunctionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LParenToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LSBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RParenToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RSBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.SemicolonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.StringToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
import net.buildabrowser.babbrowser.cssbase.tokens.URLToken;
import net.buildabrowser.babbrowser.cssbase.tokens.WhitespaceToken;

public class CSSTokenizerTest {

  private CSSTokenizer cssTokenizer;

  @BeforeEach
  public void beforeEach() {
    this.cssTokenizer = CSSTokenizer.create();
  }

  @Test
  @DisplayName("Can tokenize a whitespace token")
  public void canTokenizeAWhitespaceToken() throws IOException {
    CSSTokenizerInput input = stringInput("\r\n \t");
    Token token = cssTokenizer.consumeAToken(input);
    Assertions.assertEquals(WhitespaceToken.create(), token);
    
    // Ensure it was as much whitespace as possible
    token = cssTokenizer.consumeAToken(input);
    Assertions.assertEquals(EOFToken.create(), token);
  }

  @Test
  @DisplayName("Can tokenize a hash token")
  public void canTokenizeAHashToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("#bab"));
    Assertions.assertEquals(
      HashToken.create("bab", HashToken.Type.ID),
      token);
  }

  @Test
  @DisplayName("Can tokenize a colon token")
  public void canTokenizeAColonToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput(":"));
    Assertions.assertEquals(ColonToken.create(), token);
  }

  @Test
  @DisplayName("Can tokenize a semicolon token")
  public void canTokenizeASemicolonToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput(";"));
    Assertions.assertEquals(SemicolonToken.create(), token);
  }

  @Test
  @DisplayName("Can tokenize a left square bracket token")
  public void canTokenizeALeftSquareBracketToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("["));
    Assertions.assertEquals(LSBracketToken.create(), token);
  }

  @Test
  @DisplayName("Can tokenize a right square bracket token")
  public void canTokenizeARightSquareBracketToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("]"));
    Assertions.assertEquals(RSBracketToken.create(), token);
  }

  @Test
  @DisplayName("Can tokenize a left curly bracket token")
  public void canTokenizeALeftCurlyBracketToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("{"));
    Assertions.assertEquals(LCBracketToken.create(), token);
  }

  @Test
  @DisplayName("Can tokenize a right curly bracket token")
  public void canTokenizeARightCurlyBracketToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("}"));
    Assertions.assertEquals(RCBracketToken.create(), token);
  }

  @Test
  @DisplayName("Can tokenize a left parentheses token")
  public void canTokenizeALeftParenthesesToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("("));
    Assertions.assertEquals(LParenToken.create(), token);
  }

  @Test
  @DisplayName("Can tokenize a right parentheses token")
  public void canTokenizeARightParenthesesToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput(")"));
    Assertions.assertEquals(RParenToken.create(), token);
  }

  @Test
  @DisplayName("Can tokenize an ident token")
  public void canTokenizeAnIdentToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("color"));
    Assertions.assertEquals(IdentToken.create("color"), token);
  }

  @Test
  @DisplayName("Can tokenize an at-keyword token")
  public void canTokenizeAnAtKeywordToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("@media"));
    Assertions.assertEquals(AtKeywordToken.create("media"), token);
  }

  @Test
  @DisplayName("Can tokenize a function token")
  public void canTokenizeAFunctionToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("rgb("));
    Assertions.assertEquals(FunctionToken.create("rgb"), token);
  }

  @Test
  @DisplayName("Can tokenize an end of file token")
  public void canTokenizeAnEOFToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput(""));
    Assertions.assertEquals(EOFToken.create(), token);
  }

  @Test
  @DisplayName("Can tokenize a number token")
  public void canTokenizeANumberToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("5"));
    Assertions.assertEquals(NumberToken.create(5), token);
  }

  @Test
  @DisplayName("Can tokenize a floating number token")
  public void canTokenizeAFloatingNumberToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("+51.3e-1"));
    Assertions.assertEquals(NumberToken.create(5.13, false, true), token);
  }

  @Test
  @DisplayName("Can tokenize a dimension token")
  public void canTokenizeADimensionToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("5em"));
    Assertions.assertEquals(DimensionToken.create(5, "em"), token);
  }

  @Test
  @DisplayName("Can tokenize a percentage token")
  public void canTokenizeAPercentageToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("5%"));
    Assertions.assertEquals(PercentageToken.create(5), token);
  }

  @Test
  @DisplayName("Can tokenize an escaped ident token")
  public void canTokenizeAnEscapedIdentToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("\\{\\0061\\}"));
    Assertions.assertEquals(IdentToken.create("{a}"), token);
  }

  @Test
  @DisplayName("Can tokenize a string token")
  public void canTokenizeAStringToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("\"Hello World\""));
    Assertions.assertEquals(StringToken.create("Hello World"), token);
  }

  @Test
  @DisplayName("Can tokenize a bad string token")
  public void canTokenizeABadStringToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("\"Hello\n World\""));
    Assertions.assertEquals(BadStringToken.create(), token);
  }

  @Test
  @DisplayName("Can escape a newline in a string token")
  public void canEscapeANewlineInAStringToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("\"Hello\\\nWorld\""));
    Assertions.assertEquals(StringToken.create("HelloWorld"), token);
  }

  @Test
  @DisplayName("Can tokenize an escaped string token")
  public void canTokenizeAnEscapedStringToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("'\\{\\0061\\}'"));
    Assertions.assertEquals(StringToken.create("{a}"), token);
  }

  @Test
  @DisplayName("Can ignore comments")
  public void canIgnoreComments() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("/*Gorillas*/;"));
    Assertions.assertEquals(SemicolonToken.create(), token);
  }

  @Test
  @DisplayName("Can tokenize a URL token")
  public void canTokenizeAURLToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("url(index.css)"));
    Assertions.assertEquals(URLToken.create("index.css"), token);

    token = cssTokenizer.consumeAToken(stringInput("url( index.css )"));
    Assertions.assertEquals(URLToken.create("index.css"), token);
  }

  @Test
  @DisplayName("Can tokenize a URL function")
  public void canTokenizeAURLFunction() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("url(\"index.css\")"));
    Assertions.assertEquals(new FunctionToken("url"), token);
    token = cssTokenizer.consumeAToken(stringInput("url( \"index.css\" )"));
    Assertions.assertEquals(new FunctionToken("url"), token);
  }
  
  private static CSSTokenizerInput stringInput(String input) {
    return CSSTokenizerInput.fromReader(new StringReader(input));
  }

}
