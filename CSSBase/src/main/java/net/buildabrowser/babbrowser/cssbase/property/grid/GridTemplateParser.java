package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.MutablePropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.ManyResult;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridTemplateValue.GridTemplateLineValue;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class GridTemplateParser implements PropertyValueParser {

  private static final CSSFailure EXPECTED_SLASH = new CSSFailure(
    "Expected a delimiter token with the '/' value!");

  private final GridTrackListParser gridTrackListParser
    = new GridTrackListParser(null);

  private final GridTemplateAreasParser gridTemplateAreasParser
    = new GridTemplateAreasParser();
  
  private final GridTrackSizeParser gridTrackSizeParser
    = new GridTrackSizeParser(false);

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("none")
    ) {
      return GridTemplateValue.NONE;
    }

    return PropertyValueParserUtil.parseLongest(stream,
      this::parseRowsColumns,
      this::parseAreaLinesColumns);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.GRID_TEMPLATE;
  }

  @Override
  public void updateProperty(
    CSSValue result,
    MutablePropertyContainer propertySetter
  ) {
    GridTemplateValue template = (GridTemplateValue) result;
    propertySetter.setProperty(CSSProperty.GRID_TEMPLATE_ROWS, template.rows());
    propertySetter.setProperty(CSSProperty.GRID_TEMPLATE_COLUMNS, template.columns());
    propertySetter.setProperty(CSSProperty.GRID_TEMPLATE_AREAS, template.areas());
  }

  private CSSValue parseRowsColumns(
    SeekableCSSTokenStream stream
  ) throws IOException {
    CSSValue rowsValue = gridTrackListParser.parse(stream);
    if (rowsValue.isFailure()) return rowsValue;

    if (!(
      stream.read() instanceof DelimToken delimToken
      && delimToken.ch() == '/'
    )) return EXPECTED_SLASH;

    CSSValue columnsValue = gridTrackListParser.parse(stream);
    if (columnsValue.isFailure()) return columnsValue;

    return GridTemplateValue.create(
      rowsValue, columnsValue, CSSValue.NONE);
  }

  private CSSValue parseAreaLinesColumns(
    SeekableCSSTokenStream stream
  ) throws IOException {
    CSSValue areaLinesResult = PropertyValueParserUtil.parseOneOrMore(stream, this::parseAreaLines);
    if (areaLinesResult.isFailure()) return areaLinesResult;

    List<CSSValue> areaLines = ((ManyResult) areaLinesResult).values();

    List<CSSValue> gridTrackRows = new ArrayList<>();
    List<CSSValue> gridTemplateAreas = new ArrayList<>();
    List<String> gridLineNames = new ArrayList<>();
    for (CSSValue areaLineValue: areaLines) {
      GridTemplateLineValue value = (GridTemplateLineValue) areaLineValue;
      gridTemplateAreas.add(value.rowArea());
      gridLineNames.addAll(value.startLines());
      gridTrackRows.add(GridTrackValue.create(
        List.copyOf(gridLineNames), value.trackSize()));
      gridLineNames.clear();
      gridLineNames.addAll(value.endLines());
    }

    if (gridLineNames.size() != 0) {
      gridTrackRows.add(GridTrackValue.create(
        List.copyOf(gridLineNames), null));
    }

    CSSValue templateColumns = CSSValue.NONE;
    if (
      stream.peek() instanceof DelimToken delimToken
      && delimToken.ch() == '/'
    ) {
      stream.read();
      templateColumns = gridTrackListParser.parseAutoTrackList(stream, false);
      if (templateColumns.isFailure()) return templateColumns;
    }

    return GridTemplateValue.create(
      GridTrackListValue.create(gridTrackRows, null),
      templateColumns,
      GridTemplateAreasValue.create(gridTemplateAreas));
  }

  private CSSValue parseAreaLines(
    SeekableCSSTokenStream stream
  ) throws IOException {
    List<String> startLines = new ArrayList<>();
    CSSValue startLinesResult = gridTrackListParser.parseLineNames(stream, startLines);
    if (startLinesResult != null) return startLinesResult;

    CSSValue rowValue = gridTemplateAreasParser.parseRow(stream);
    if (rowValue.isFailure()) return rowValue;

    CSSValue trackSize = PropertyValueParserUtil.parseMaybe(
      stream, gridTrackSizeParser::parseTrackSize);
    if (trackSize.isFailure()) {
      trackSize = CSSValue.AUTO;
    }

    List<String> endLines = new ArrayList<>();
    CSSValue endLinesResult = gridTrackListParser.parseLineNames(stream, endLines);
    if (endLinesResult != null) return endLinesResult;

    return GridTemplateLineValue.create(
      startLines, rowValue, trackSize, endLines);
  }
  
}
