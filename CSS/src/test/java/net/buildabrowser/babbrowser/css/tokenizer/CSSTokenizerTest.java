package net.buildabrowser.babbrowser.css.tokenizer;

import java.io.IOException;
import java.io.StringReader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.css.tokens.ColonToken;
import net.buildabrowser.babbrowser.css.tokens.EOFToken;
import net.buildabrowser.babbrowser.css.tokens.IdentToken;
import net.buildabrowser.babbrowser.css.tokens.LCBracketToken;
import net.buildabrowser.babbrowser.css.tokens.RCBracketToken;
import net.buildabrowser.babbrowser.css.tokens.SemicolonToken;
import net.buildabrowser.babbrowser.css.tokens.Token;
import net.buildabrowser.babbrowser.css.tokens.WhitespaceToken;

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
  
  private static CSSTokenizerInput stringInput(String input) {
    return CSSTokenizerInput.fromReader(new StringReader(input));
  }

}
