package net.buildabrowser.babbrowser.cssbase.media.parser;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.buildabrowser.babbrowser.cssbase.intermediate.SimpleBlock;
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
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue;
import net.buildabrowser.babbrowser.cssbase.property.size.LengthValue.LengthType;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DimensionToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LParenToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class CSSMediaQueryParserTest {

  private static final MediaNode INVALID_QUERY = NotMediaNode.create(
    MediaTypeNode.create("all"));

  @Test
  @DisplayName("Can parse empty media query")
  public void canParseEmptyMediaQuery() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting();
    MediaNode expected = AnyMediaNode.create();
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }
  
  @Test
  @DisplayName("Can parse media type")
  public void canParseMediaType() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
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
    CSSTokenStream stream = CSSTokenStream.createForTesting(
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
    CSSTokenStream stream = CSSTokenStream.createForTesting(
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
    CSSTokenStream stream = CSSTokenStream.createForTesting(
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
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      block(IdentToken.create("width")));
    MediaNode expected = AnyMediaNode.create(
      AndMediaNode.create(
        FeatureExistsMediaNode.create(MediaFeature.WIDTH)));
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can parse media-feature-equals")
  public void canParseMediaFeatureEquals() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      block(
        IdentToken.create("width"),
        ColonToken.create(),
        DimensionToken.create(10, "px")));
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
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      block(
        IdentToken.create("min-width"),
        ColonToken.create(),
        DimensionToken.create(10, "px")));
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
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("screen"),
      IdentToken.create("AND"),
      block(
        IdentToken.create("min-width"),
        ColonToken.create(),
        DimensionToken.create(10, "px")),
      IdentToken.create("and"),
      block(
        IdentToken.create("max-height"),
        ColonToken.create(),
        DimensionToken.create(5, "em")));
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
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      IdentToken.create("screen"),
      block(IdentToken.create("width")));
    MediaNode expected = AnyMediaNode.create(INVALID_QUERY);
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can not parse media query without AND between two features")
  public void canParseMediaQueryWithoutAndBetweenTwoFeatures() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      block(IdentToken.create("width")),
      block(IdentToken.create("height")));
    MediaNode expected = AnyMediaNode.create(INVALID_QUERY);
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }

  @Test
  @DisplayName("Can recover from invalid media query")
  public void canRecoverFromInvalidMediaQuery() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      LParenToken.create(),
      IdentToken.create("width"),
      CommaToken.create(),
      block(IdentToken.create("height")));
    MediaNode expected = AnyMediaNode.create(
      INVALID_QUERY,
      AndMediaNode.create(
        FeatureExistsMediaNode.create(MediaFeature.HEIGHT)));
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }
  
  @Test
  @DisplayName("Can parse media-feature with comparator syntax")
  public void canParseMediaFeatureWithComparatorSyntax() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      block(
        IdentToken.create("width"),
        DelimToken.create('>'),
        DelimToken.create('='),
        DimensionToken.create(15, "px")));
    MediaNode expected = AnyMediaNode.create(
      AndMediaNode.create(
        FeatureComparisonMediaNode.create(
          MediaFeature.WIDTH,
          MediaFeatureComparison.GTE,
          LengthValue.create(15, true, LengthType.PX))));
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }

    @Test
  @DisplayName("Can parse media-feature with two-comparator syntax")
  public void canParseMediaFeatureWithTwoComparatorSyntax() throws IOException {
    CSSTokenStream stream = CSSTokenStream.createForTesting(
      block(
        DimensionToken.create(11, "px"),
        DelimToken.create('<'),
        DelimToken.create('='),
        IdentToken.create("width"),
        DelimToken.create('<'),
        DelimToken.create('='),
        DimensionToken.create(15, "px")));
    MediaNode expected = AnyMediaNode.create(
      AndMediaNode.create(
        AndMediaNode.create(
          FeatureComparisonMediaNode.create(
            LengthValue.create(11, true, LengthType.PX),
            MediaFeatureComparison.LTE,
            MediaFeature.WIDTH),
          FeatureComparisonMediaNode.create(
            MediaFeature.WIDTH,
            MediaFeatureComparison.LTE,
            LengthValue.create(15, true, LengthType.PX)))));
    MediaNode actual = CSSMediaQueryParser.parseQuery(stream);
    Assertions.assertEquals(expected, actual);
  }

  private static SimpleBlock block(Token... tokens) {
    return new SimpleBlock(new LParenToken(), List.of(tokens));
  }

}
