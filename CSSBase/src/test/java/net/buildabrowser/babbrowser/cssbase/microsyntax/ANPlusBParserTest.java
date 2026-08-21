package net.buildabrowser.babbrowser.cssbase.microsyntax;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;

public class ANPlusBParserTest {
  
  @Test
  @DisplayName("Can parse odd keyword")
  public void canParseOddKeyword() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("odd"));
    ANPlusB expected = ANPlusB.ODD;
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
  }
  
  @Test
  @DisplayName("Can parse even keyword")
  public void canParseEvenKeyword() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("even"));
    ANPlusB expected = ANPlusB.EVEN;
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse integer")
  public void canParseInteger() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      NumberToken.create(5));
    ANPlusB expected = ANPlusB.create(0, 5);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }

  @Test
  @DisplayName("Can parse n-dimension")
  public void canParseNDimension() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      DimensionToken.create(5, "n"));
    ANPlusB expected = ANPlusB.create(5, 0);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }

  @Test
  @DisplayName("Can parse +n")
  public void canParsePlusN() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      DelimToken.create('+'), IdentToken.create("n"));
    ANPlusB expected = ANPlusB.create(1, 0);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }

  @Test
  @DisplayName("Can parse n")
  public void canParseN() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("n"));
    ANPlusB expected = ANPlusB.create(1, 0);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }

  @Test
  @DisplayName("Can parse -n")
  public void canParseMinusN() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("-n"));
    ANPlusB expected = ANPlusB.create(-1, 0);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }

  @Test
  @DisplayName("Can parse ndashdigit-dimension")
  public void canParseNDashDigitDimension() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      DimensionToken.create(5, "n-10"));
    ANPlusB expected = ANPlusB.create(5, -10);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }

  @Test
  @DisplayName("Can parse +ndashdigit-ident")
  public void canParsePlusNDashDigitIdent() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      DelimToken.create('+'),
      IdentToken.create("n-10"));
    ANPlusB expected = ANPlusB.create(1, -10);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }

  @Test
  @DisplayName("Can parse dashndashdigit-ident")
  public void canParsePlusdDashDigitIdent() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("-n-10"));
    ANPlusB expected = ANPlusB.create(-1, -10);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }
  
  @Test
  @DisplayName("Can parse n-dimension signed-integer")
  public void canParseNDimensionSignedInteger() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      DimensionToken.create(5, "n"),
      NumberToken.create(6, true, true));
    ANPlusB expected = ANPlusB.create(5, 6);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }

  @Test
  @DisplayName("Can parse +n signed-integer")
  public void canParsePlusNSignedInteger() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      DelimToken.create('+'), IdentToken.create("n"),
      NumberToken.create(6, true, true));
    ANPlusB expected = ANPlusB.create(1, 6);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }

  @Test
  @DisplayName("Can parse -n signed-integer")
  public void canParseMinusNSignedInteger() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("-n"),
      NumberToken.create(6, true, true));
    ANPlusB expected = ANPlusB.create(-1, 6);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }

  
  
  @Test
  @DisplayName("Can parse ndash-dimension signless-integer")
  public void canParseNDashDimensionSignlessInteger() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      DimensionToken.create(5, "n-"),
      NumberToken.create(6, true, false));
    ANPlusB expected = ANPlusB.create(5, -6);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }

  @Test
  @DisplayName("Can parse +n- signless-integer")
  public void canParsePlusNDashSignlessInteger() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      DelimToken.create('+'), IdentToken.create("n-"),
      NumberToken.create(6, true, false));
    ANPlusB expected = ANPlusB.create(1, -6);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }

  @Test
  @DisplayName("Can parse -n- signless-integer")
  public void canParseMinusNDashSignlessInteger() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("-n-"),
      NumberToken.create(6, true, false));
    ANPlusB expected = ANPlusB.create(-1, -6);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }
  
  @Test
  @DisplayName("Can parse n-dimension delim signless-integer")
  public void canParseNDimensionDelimSignlessInteger() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      DimensionToken.create(5, "n"),
      DelimToken.create('-'),
      NumberToken.create(6, true, false));
    ANPlusB expected = ANPlusB.create(5, -6);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }

  @Test
  @DisplayName("Can parse +n delim signless-integer")
  public void canParsePlusNDelimSignlessInteger() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      DelimToken.create('+'), IdentToken.create("n"),
      DelimToken.create('-'),
      NumberToken.create(6, true, false));
    ANPlusB expected = ANPlusB.create(1, -6);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }

  @Test
  @DisplayName("Can parse -n delim signless-integer")
  public void canParseMinusNDelimSignlessInteger() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("-n"),
      DelimToken.create('+'),
      NumberToken.create(6, true, false));
    ANPlusB expected = ANPlusB.create(-1, 6);
    ANPlusB actual = ANPlusBParser.parse(stream);
    Assertions.assertEquals(expected, actual);
    Assertions.assertInstanceOf(EOFToken.class, stream.peek());
  }

}
