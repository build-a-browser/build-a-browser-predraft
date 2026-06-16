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
import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.tokens.ColonToken;
import net.buildabrowser.babbrowser.cssbase.tokens.CommaToken;
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
    SeekableCSSTokenStream stream
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
    SeekableCSSTokenStream stream
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
    SeekableCSSTokenStream stream
  ) throws IOException {
    if (!(
      stream.peek() instanceof SimpleBlock simpleBlock
      && simpleBlock.type() instanceof LParenToken
    )) return null;
    stream.read();

    SeekableCSSTokenStream innerStream = ListCSSTokenStream.createWithSkippedWhitespace(
      stream.source(), simpleBlock.value());

    if (!(
      innerStream.peek() instanceof IdentToken identToken
    )) return null;
    innerStream.read();

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
    if (innerStream.peek() instanceof ColonToken) {
      innerStream.read();
      if (
        !comparison.equals(MediaFeatureComparison.EQ)
        && !feature.allowMinMax()
      ) return null;
      
      CSSValue targetValue = parseFeatureTarget(feature, innerStream);
      node = FeatureComparisonMediaNode.create(
        feature, comparison, targetValue);
    } else if (comparison.equals(MediaFeatureComparison.EQ)) {
      node = FeatureExistsMediaNode.create(feature);
    }
    if (node == null) return null;
    if (!(innerStream.peek() instanceof EOFToken)) return null;

    return node;
  }

  private static CSSValue parseFeatureTarget(
    MediaFeature feature, SeekableCSSTokenStream stream
  ) throws IOException {
    // TODO: Depends on feature
    CSSValue value = SIZE_PARSER.parse(stream);
    if (value.isFailure()) return null;
    return value;
  }

}
