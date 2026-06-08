package net.buildabrowser.babbrowser.cssbase.media.parser;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.media.ast.AndMediaNode;
import net.buildabrowser.babbrowser.cssbase.media.ast.AnyMediaNode;
import net.buildabrowser.babbrowser.cssbase.media.ast.FeatureComparisonMediaNode;
import net.buildabrowser.babbrowser.cssbase.media.ast.FeatureComparisonMediaNode.MediaFeatureComparison;
import net.buildabrowser.babbrowser.cssbase.media.ast.FeatureExistsMediaNode;
import net.buildabrowser.babbrowser.cssbase.media.ast.MediaFeature;
import net.buildabrowser.babbrowser.cssbase.media.ast.MediaNode;
import net.buildabrowser.babbrowser.cssbase.media.ast.MediaTypeNode;
import net.buildabrowser.babbrowser.cssbase.media.ast.NotMediaNode;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LParenToken;
import net.buildabrowser.babbrowser.cssbase.tokens.RParenToken;

public class CSSMediaQueryParserTest {

  private static final MediaNode INVALID_QUERY = NotMediaNode.create(
    MediaTypeNode.create("all"));

  @Test
  @DisplayName("Can parse empty media query")
  public void canParseEmptyMediaQuery() throws IOException {
    SeekableCSSTokenStream stream = CSSTokenStream.createForTesting();
    MediaNode expected = AnyMediaNode.create();
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }
  
  @Test
  @DisplayName("Can parse media type")
  public void canParseMediaType() throws IOException {
    SeekableCSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("screen"));
    MediaNode expected = AnyMediaNode.create(
      AndMediaNode.create(
        MediaTypeNode.create("screen")));
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }
  
  @Test
  @DisplayName("Can parse only media type")
  public void canParseOnlyMediaType() throws IOException {
    SeekableCSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("only"),
      IdentToken.create("screen"));
    MediaNode expected = AnyMediaNode.create(
      AndMediaNode.create(
        MediaTypeNode.create("screen")));
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }
  
  @Test
  @DisplayName("Can parse not media type")
  public void canParseNotMediaType() throws IOException {
    SeekableCSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("not"),
      IdentToken.create("screen"));
    MediaNode expected = AnyMediaNode.create(
      AndMediaNode.create(
        NotMediaNode.create(MediaTypeNode.create("screen"))));
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse multiple media queries")
  public void canParseMultipleMediaQueries() throws IOException {
    SeekableCSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("screen"),
      CommaToken.create(),
      IdentToken.create("print"));
    MediaNode expected = AnyMediaNode.create(
      AndMediaNode.create(
        MediaTypeNode.create("screen")),
      AndMediaNode.create(
        MediaTypeNode.create("print")));
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse media-feature-exists")
  public void canParseMediaFeatureExists() throws IOException {
    SeekableCSSTokenStream stream = CSSTokenStream.createForTesting(
      LParenToken.create(),
      IdentToken.create("width"),
      RParenToken.create());
    MediaNode expected = AnyMediaNode.create(
      AndMediaNode.create(
        FeatureExistsMediaNode.create(MediaFeature.WIDTH)));
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse media-feature-equals")
  public void canParseMediaFeatureEquals() throws IOException {
    SeekableCSSTokenStream stream = CSSTokenStream.createForTesting(
      LParenToken.create(),
      IdentToken.create("width"),
      ColonToken.create(),
      DimensionToken.create(10, "px"),
      RParenToken.create());
    MediaNode expected = AnyMediaNode.create(
      AndMediaNode.create(
        FeatureComparisonMediaNode.create(
          MediaFeature.WIDTH,
          MediaFeatureComparison.EQ,
          LengthValue.create(10, true, LengthType.PX))));
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse media-feature-min")
  public void canParseMediaFeatureMin() throws IOException {
    SeekableCSSTokenStream stream = CSSTokenStream.createForTesting(
      LParenToken.create(),
      IdentToken.create("min-width"),
      ColonToken.create(),
      DimensionToken.create(10, "px"),
      RParenToken.create());
    MediaNode expected = AnyMediaNode.create(
      AndMediaNode.create(
        FeatureComparisonMediaNode.create(
          MediaFeature.WIDTH,
          MediaFeatureComparison.GTE,
          LengthValue.create(10, true, LengthType.PX))));
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse media query with AND")
  public void canParseMediaQueryWithAnd() throws IOException {
    SeekableCSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("screen"),
      IdentToken.create("AND"),
      LParenToken.create(),
      IdentToken.create("min-width"),
      ColonToken.create(),
      DimensionToken.create(10, "px"),
      RParenToken.create(),
      IdentToken.create("and"),
      LParenToken.create(),
      IdentToken.create("max-height"),
      ColonToken.create(),
      DimensionToken.create(5, "em"),
      RParenToken.create());
    MediaNode expected = AnyMediaNode.create(
      AndMediaNode.create(
        MediaTypeNode.create("screen"),
        FeatureComparisonMediaNode.create(
          MediaFeature.WIDTH,
          MediaFeatureComparison.GTE,
          LengthValue.create(10, true, LengthType.PX)),
        FeatureComparisonMediaNode.create(
          MediaFeature.HEIGHT,
          MediaFeatureComparison.LTE,
          LengthValue.create(5, true, LengthType.EM))));
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can not parse media query without AND between type and feature")
  public void canParseMediaQueryWithoutAndBetweenTypeAndFeature() throws IOException {
    SeekableCSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("screen"),
      LParenToken.create(),
      IdentToken.create("width"),
      RParenToken.create());
    MediaNode expected = AnyMediaNode.create(INVALID_QUERY);
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can not parse media query without AND between two features")
  public void canParseMediaQueryWithoutAndBetweenTwoFeatures() throws IOException {
    SeekableCSSTokenStream stream = CSSTokenStream.createForTesting(
      LParenToken.create(),
      IdentToken.create("width"),
      RParenToken.create(),
      LParenToken.create(),
      IdentToken.create("height"),
      RParenToken.create());
    MediaNode expected = AnyMediaNode.create(INVALID_QUERY);
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can recover from invalid media query")
  public void canRecoverFromInvalidMediaQuery() throws IOException {
    SeekableCSSTokenStream stream = CSSTokenStream.createForTesting(
      LParenToken.create(),
      IdentToken.create("width"),
      CommaToken.create(),
      LParenToken.create(),
      IdentToken.create("height"),
      RParenToken.create());
    MediaNode expected = AnyMediaNode.create(
      INVALID_QUERY,
      AndMediaNode.create(
        FeatureExistsMediaNode.create(MediaFeature.HEIGHT)));
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }

}
