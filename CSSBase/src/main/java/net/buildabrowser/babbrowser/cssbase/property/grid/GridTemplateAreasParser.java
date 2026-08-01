package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.ManyResult;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateAreasValue.GridTemplateAreasRowValue;
import net.buildabrowser.babbrowser.cssbase.tokenizer.imp.TokenizerUtil;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.StringToken;

public class GridTemplateAreasParser implements PropertyValueParser {

  private static final CSSFailure INVALID_CELL = new CSSFailure(
    "Invalid name for template area cell!");

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("none")
    ) {
      return CSSValue.NONE;
    }

    CSSValue value = PropertyValueParserUtil.parseOneOrMore(
      stream, this::parseRow);
    if (value.isFailure()) return value;
    List<CSSValue> rowList = ((ManyResult) value).values();
    return GridTemplateAreasValue.create(rowList);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.GRID_TEMPLATE_AREAS;
  }
  
  public CSSValue parseRow(CSSTokenStream stream) throws IOException {
    if (!(
      stream.read() instanceof StringToken stringToken
    )) return CSSFailure.EXPECTED_STRING;

    return parseRow(stringToken.value());
  }

  public CSSValue parseRow(String rowValue) {
    List<String> cellNames = new ArrayList<>();
    int[] index = new int[1];
    while (index[0] < rowValue.length()) {
      int ch = rowValue.codePointAt(index[0]);
      if (TokenizerUtil.isIdentCodePoint(ch)) {
        cellNames.add(consumeName(rowValue, index));
      } else if (ch == '.') {
        cellNames.add(null);
        skipEmpty(rowValue, index);
      } else if (TokenizerUtil.isWhiteSpace(ch)) {
        skipWhitespace(rowValue, index);
      } else {
        return INVALID_CELL;
      }
    }

    return GridTemplateAreasRowValue.create(cellNames);
  }

  private String consumeName(String rowValue, int[] index) {
    int startIndex = index[0];
    plusOne(rowValue, index);
    while (
      index[0] < rowValue.length()
      && TokenizerUtil.isIdentCodePoint(rowValue.codePointAt(index[0]))
    ) plusOne(rowValue, index);

    return rowValue.substring(startIndex, index[0]);
  }

  private void skipEmpty(String rowValue, int[] index) {
    plusOne(rowValue, index);
    while (
      index[0] < rowValue.length()
      && rowValue.codePointAt(index[0]) == '.'
    ) plusOne(rowValue, index);
  }

  private void skipWhitespace(String rowValue, int[] index) {
    plusOne(rowValue, index);
    while (
      index[0] < rowValue.length()
      && TokenizerUtil.isWhiteSpace(rowValue.codePointAt(index[0]))
    ) plusOne(rowValue, index);
  }

  private void plusOne(String rowValue, int[] index) {
    index[0] = Character.offsetByCodePoints(
      rowValue, index[0], 1);
  }
  
}
