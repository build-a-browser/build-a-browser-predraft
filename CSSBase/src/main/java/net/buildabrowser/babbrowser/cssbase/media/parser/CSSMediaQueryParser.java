package net.buildabrowser.babbrowser.cssbase.media.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValueOrFeature;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.LParenToken;

public final class CSSMediaQueryParser {

  private static final SizeParser SIZE_PARSER = new SizeParser(
    false, false, false, false, null);
  private static final MediaNode INVALID_QUERY = NotMediaNode.create(
    MediaTypeNode.create("all"));
  
  private CSSMediaQueryParser() {}

  public static AnyMediaNode parseQuery(
    CSSTokenStream stream
  ) throws IOException {
    List<MediaNode> queries = new ArrayList<>();
    while (!(stream.peek() instanceof EOFToken)) {
      MediaNode query = parseSingleQuery(stream);
      queries.add(query == null ? INVALID_QUERY : query);
      while (!(
        stream.peek() instanceof EOFToken
        || stream.peek() instanceof CommaToken
      )) stream.read();
      if (stream.peek() instanceof CommaToken) {
        stream.read();
      }
    }

    return AnyMediaNode.create(queries);
  }

  private static AndMediaNode parseSingleQuery(
    CSSTokenStream stream
  ) throws IOException {
    List<MediaNode> queries = new ArrayList<>();
    boolean expectAnd = false;
    if (stream.peek() instanceof IdentToken) {
      MediaNode node = parseMediaType(stream);
      if (node == null) return null;
      queries.add(node);
      expectAnd = true;
    }

    while (!(
      stream.peek() instanceof EOFToken
      || stream.peek() instanceof CommaToken
    )) {
      if (expectAnd && !parseAnd(stream)) {
        return null;
      }

      MediaNode node = parseExpression(stream);
      if (node == null) return null;
      queries.add(node);

      expectAnd = true;
    }

    return AndMediaNode.create(queries);
  }

  private static MediaNode parseMediaType(
    CSSTokenStream stream
  ) throws IOException {
    boolean negateType = false;
    if (
      stream.peek() instanceof IdentToken identToken
      && (
        identToken.value().equalsIgnoreCase("ONLY")
        || (negateType = identToken.value().equalsIgnoreCase("NOT")))
    ) {
      stream.read();
    }

    if (!(
      stream.peek() instanceof IdentToken identToken
    )) return null;
    stream.read();

    MediaNode typeNode = MediaTypeNode.create(identToken.value().toLowerCase());
    return negateType ?
      NotMediaNode.create(typeNode) :
      typeNode;
  }

  private static boolean parseAnd(
    CSSTokenStream stream
  ) throws IOException {
    if (!(
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equalsIgnoreCase("AND")
    )) return false;
    stream.read();
    return true;
  }

  private static MediaNode parseExpression(
    CSSTokenStream stream
  ) throws IOException {
    if (!(
      stream.peek() instanceof SimpleBlock simpleBlock
      && simpleBlock.type() instanceof LParenToken
    )) return null;
    stream.read();

    CSSTokenStream innerStream = ListCSSTokenStream.createWithSkippedWhitespace(
      stream.source(), simpleBlock.value());
    
    int mark = innerStream.mark();
    MediaNode parsedNode = parseTraditionalExpression(innerStream);
    if (parsedNode != null) {
      return parsedNode;
    }
    innerStream.restoreMark(mark);

    mark = innerStream.mark();
    parsedNode = parseNewExpression(innerStream);
    if (parsedNode != null) {
      return parsedNode;
    }
    innerStream.restoreMark(mark);

    return parseTwoOpExpression(innerStream);
  }

  private static MediaNode parseTraditionalExpression(
    CSSTokenStream stream
  ) throws IOException {
    if (!(
      stream.peek() instanceof IdentToken identToken
    )) return null;
    stream.read();

    MediaFeatureComparison comparison = MediaFeatureComparison.EQ;
    String name = identToken.value();
    if (name.startsWith("min-")) {
      comparison = MediaFeatureComparison.GTE;
      name = name.substring(4);
    } else if (name.startsWith("max-")) {
      comparison = MediaFeatureComparison.LTE;
      name = name.substring(4);
    }

    MediaFeature feature = MediaFeature.byName(name);
    if (feature == null) return null;

    MediaNode node = null;
    if (stream.peek() instanceof ColonToken) {
      stream.read();
      if (
        !comparison.equals(MediaFeatureComparison.EQ)
        && !feature.allowMinMax()
      ) return null;
      
      CSSValue targetValue = parseFeatureTarget(feature, stream);
      node = FeatureComparisonMediaNode.create(
        feature, comparison, targetValue);
    } else if (comparison.equals(MediaFeatureComparison.EQ)) {
      node = FeatureExistsMediaNode.create(feature);
    }
    if (node == null) return null;
    if (!(stream.peek() instanceof EOFToken)) return null;

    return node;
  }

  private static MediaNode parseNewExpression(
    CSSTokenStream stream
  ) throws IOException {
    CSSValueOrFeature valueA = parseFeatureOrValue(stream);
    if (valueA == null) return null;

    MediaFeatureComparison comparison = parseComparisonOperator(stream);
    if (comparison == null) return null;

    CSSValueOrFeature valueB = parseFeatureOrValue(stream);
    if (valueB == null) return null;
    MediaNode node = FeatureComparisonMediaNode.create(
      valueA, comparison, valueB);

    if (node == null) return null;
    if (!(stream.peek() instanceof EOFToken)) return null;

    return node;
  }

  private static MediaNode parseTwoOpExpression(
    CSSTokenStream stream
  ) throws IOException {
    // TODO: Need to pass valid feature
    CSSValue valueA = parseFeatureTarget(null, stream);
    if (valueA == null) return null;

    MediaFeatureComparison comparison1 = parseComparisonOperator(stream);
    if (comparison1 == null) return null;

    CSSValueOrFeature valueB = parseFeatureOrValue(stream);
    if (valueB == null) return null;
    if (!(valueB instanceof MediaFeature mediaFeature)) return null;

    MediaFeatureComparison comparison2 = parseComparisonOperator(stream);
    if (comparison2 == null) return null;
    
    CSSValue valueC = parseFeatureTarget(null, stream);
    if (valueC == null) return null;

    if (!(stream.peek() instanceof EOFToken)) return null;

    boolean isLeftLt =
      comparison1.equals(MediaFeatureComparison.LT)
      || comparison1.equals(MediaFeatureComparison.LTE);
    boolean isRightLt =
      comparison2.equals(MediaFeatureComparison.LT)
      || comparison2.equals(MediaFeatureComparison.LTE);
    boolean isLeftGt =
      comparison1.equals(MediaFeatureComparison.GT)
      || comparison1.equals(MediaFeatureComparison.GTE);
    boolean isRightGt =
      comparison2.equals(MediaFeatureComparison.GT)
      || comparison2.equals(MediaFeatureComparison.GTE);
    if (!(
      (isLeftLt && isRightLt)
      || (isLeftGt && isRightGt)
    )) return null;

    return AndMediaNode.create(
      FeatureComparisonMediaNode.create(valueA, comparison1, mediaFeature),
      FeatureComparisonMediaNode.create(mediaFeature, comparison2, valueC)
    );
  }

  private static CSSValueOrFeature parseFeatureOrValue(
    CSSTokenStream stream
  ) throws IOException {
    if (
      stream.peek() instanceof IdentToken identToken
      && MediaFeature.byName(identToken.value()) instanceof MediaFeature feature
      && feature.allowMinMax()
    ) {
      stream.read();
      return feature;
    } else {
      // TODO: Need to pass a valid feature
      return parseFeatureTarget(null, stream);
    }
  }

  private static CSSValue parseFeatureTarget(
    MediaFeature feature, CSSTokenStream stream
  ) throws IOException {
    // TODO: Depends on feature
    CSSValue value = SIZE_PARSER.parse(stream);
    if (value.isFailure()) return null;
    return value;
  }

  private static MediaFeatureComparison parseComparisonOperator(
    CSSTokenStream stream
  ) throws IOException {
    if (!(stream.peek() instanceof DelimToken delimToken1)) return null;
    stream.read();
    return switch (delimToken1.ch()) {
      case '=' -> MediaFeatureComparison.EQ;
      case '>' -> consumeIfEq(stream) ?
        MediaFeatureComparison.GTE :
        MediaFeatureComparison.GT;
      case '<' -> consumeIfEq(stream) ?
        MediaFeatureComparison.LTE :
        MediaFeatureComparison.LT;
      default -> null;
    };
  }

  private static boolean consumeIfEq(CSSTokenStream stream) throws IOException {
    if (
      stream.peek() instanceof DelimToken delimToken
      && delimToken.ch() == '='
    ) {
      stream.read();
      return true;
    }

    return false;
  }

}
