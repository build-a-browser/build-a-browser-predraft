package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.AnyOrderResult;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridLineValue.CustomIdentValue;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridLineValue.LineNumberValue;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public class GridLineParser implements PropertyValueParser {

  private final CSSProperty relatedProperty;

  public GridLineParser(CSSProperty relatedProperty) {
    this.relatedProperty = relatedProperty;
  }

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    return parseLine(stream);
  }

  @Override
  public CSSProperty relatedProperty() {
    return this.relatedProperty;
  }

  public static CSSValue parseLine(
    SeekableCSSTokenStream stream
  ) throws IOException {
    Token firstToken = stream.read();
    if (
      firstToken instanceof IdentToken identToken
      && identToken.value().equals("auto")
    ) {
      return CSSValue.AUTO;
    } else if (
      firstToken instanceof IdentToken identToken
      && isCustomIdent(identToken)
      && (!(stream.peek() instanceof NumberToken))
    ) {
      return GridLineValue.create(
        false, true, 1,
        identToken.value());
    } else if (
      firstToken instanceof IdentToken identToken
      && identToken.value().equals("span")
    ) {
      return parseLineNameNum(stream, true);
    } else {
      stream.unread(firstToken);
      return parseLineNameNum(stream, false);
    }
  }

  public static CSSValue maybeParseNextLine(
    SeekableCSSTokenStream stream
  ) throws IOException {
    if (!(
      stream.peek() instanceof DelimToken delimToken
      && delimToken.ch() == '/'
    )) return null;
    stream.read();
    return parseLine(stream);
  }

  private static CSSValue parseLineNameNum(
    SeekableCSSTokenStream stream,
    boolean isSpan
  ) throws IOException {
    CSSValue anyOrderResult = PropertyValueParserUtil.parseAnyOrder(
      stream, new PropertyValueParser[] {
        GridLineParser::parseCustomIdent,
        s -> GridLineParser.parseLineNumber(s, !isSpan)
      });
    
    if (anyOrderResult.isFailure()) return anyOrderResult;
    CSSValue[] values = ((AnyOrderResult) anyOrderResult).values();
    String lineName = values[0] == null ?
      null : ((CustomIdentValue) values[0]).value();
    int lineNumber = values[1] != null ?
      ((LineNumberValue) values[1]).lineNumber() :
      1;
    return GridLineValue.create(
      isSpan, false, lineNumber, lineName);
  }

  private static CSSValue parseCustomIdent(
    SeekableCSSTokenStream stream
  ) throws IOException {
    if (
      stream.read() instanceof IdentToken identToken
      && isCustomIdent(identToken)
    ) {
      return CustomIdentValue.create(identToken.value());
    }

    return CSSFailure.EXPECTED_IDENT;
  }

  private static CSSValue parseLineNumber(
    SeekableCSSTokenStream stream,
    boolean allowNegative
  ) throws IOException {
    if (
      stream.read() instanceof NumberToken numberToken
      && numberToken.isInteger()
      && (
        numberToken.value().intValue() > 0
        || (allowNegative && numberToken.value().intValue() < 0))
    ) {
      return LineNumberValue.create(numberToken.value().intValue());
    }

    return CSSFailure.EXPECTED_INTEGER;
  }

  private static boolean isCustomIdent(IdentToken identToken) {
    return !(
      identToken.value().equals("auto")
      || identToken.value().equals("span"));
  }
  
}
