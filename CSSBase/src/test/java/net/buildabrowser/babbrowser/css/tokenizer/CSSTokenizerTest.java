package net.buildabrowser.babbrowser.css.tokenizer;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizer;
import net.buildabrowser.babbrowser.cssbase.tokenizer.CSSTokenizerInput;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.HashToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.PercentageToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RCBracketToken;
import net.buildabrowser.babbrowser.cssbase.tokens.SemicolonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;
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
  @DisplayName("Can tokenize an ident token")
  public void canTokenizeAnIdentToken() throws IOException {
    Token token = cssTokenizer.consumeAToken(stringInput("color"));
    Assertions.assertEquals(IdentToken.create("color"), token);
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
    Assertions.assertEquals(NumberToken.create(5.13, false), token);
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
  
  private static CSSTokenizerInput stringInput(String input) {
    return CSSTokenizerInput.fromReader(new StringReader(input));
  }

}
