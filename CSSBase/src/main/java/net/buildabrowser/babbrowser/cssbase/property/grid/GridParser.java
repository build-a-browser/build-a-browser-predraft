package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.MutablePropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.AnyOrderResult;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridAutoFlowValue.GridAutoFlowDirection;
import net.buildabrowser.babbrowser.cssbase.tokens.DelimToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class GridParser implements PropertyValueParser {

  private static final CSSFailure EXPECTED_SLASH = new CSSFailure(
    "Expected a delimiter token with the '/' value!");
  private static final CSSFailure EXPECTED_AUTO_FLOW = new CSSFailure(
    "Expected a ident token token with the \"auto-flow\" value!");
  private static final CSSFailure EXPECTED_DENSE = new CSSFailure(
    "Expected a ident token token with the \"dense\" value!");

  private final GridTemplateParser gridTemplateParser = new GridTemplateParser();
  private final GridTrackListParser templateTracksParser = new GridTrackListParser(null);
  private final GridAutoTracksParser autoTracksParser = new GridAutoTracksParser(null);

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    return PropertyValueParserUtil.parseLongest(stream,
      this::parseTemplate,
      this::parseRowsThenAutoColumns,
      this::parseAutoRowsThenColumns
    );
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.GRID;
  }

  @Override
  public void updateProperty(CSSValue result, MutablePropertyContainer propertySetter) {
    GridValue value = (GridValue) result;
    // Null values should automatically be set to the initial by the caller
    if (value.rows() != null) {
      propertySetter.setProperty(CSSProperty.GRID_TEMPLATE_ROWS, value.rows());
    }
    if (value.columns() != null) {
      propertySetter.setProperty(CSSProperty.GRID_TEMPLATE_COLUMNS, value.columns());
    }
    if (value.areas() != null) {
      propertySetter.setProperty(CSSProperty.GRID_TEMPLATE_AREAS, value.areas());
    }
    if (value.autoRows() != null) {
      propertySetter.setProperty(CSSProperty.GRID_AUTO_ROWS, value.autoRows());
    }
    if (value.autoColumns() != null) {
      propertySetter.setProperty(CSSProperty.GRID_AUTO_COLUMNS, value.autoColumns());
    }
    if (value.autoFlow() != null) {
      propertySetter.setProperty(CSSProperty.GRID_AUTO_FLOW, value.autoFlow());
    }
  }

  private CSSValue parseTemplate(CSSTokenStream stream) throws IOException {
    CSSValue result = gridTemplateParser.parse(stream);
    if (result.isFailure()) return result;

    GridTemplateValue template = (GridTemplateValue) result;
    return GridValue.create(
      template.rows(), template.columns(), template.areas(),
      null, null, null);
  }

  private CSSValue parseRowsThenAutoColumns(CSSTokenStream stream) throws IOException {
    CSSValue rowsValue = templateTracksParser.parse(stream);
    if (rowsValue.isFailure()) return rowsValue;

    if (!(
      stream.read() instanceof DelimToken delimToken
      && delimToken.ch() == '/'
    )) return EXPECTED_SLASH;

    CSSValue autoFlowResult = parseAutoFlowOrDense(stream);
    boolean isDense = autoFlowResult != null;
    if (isDense && autoFlowResult.isFailure()) return autoFlowResult;
    
    CSSValue columnsValue =  autoTracksParser.parse(stream);
    if (columnsValue.isFailure()) return columnsValue;

    CSSValue autoFlow = GridAutoFlowValue.create(
      GridAutoFlowDirection.COLUMN, isDense);

    return GridValue.create(
      rowsValue, null, null,
      null, columnsValue, autoFlow);
  }

  private CSSValue parseAutoRowsThenColumns(CSSTokenStream stream) throws IOException {
    CSSValue autoFlowResult = parseAutoFlowOrDense(stream);
    boolean isDense = autoFlowResult != null;
    if (isDense && autoFlowResult.isFailure()) return autoFlowResult;

    CSSValue rowsValue = autoTracksParser.parse(stream);
    if (rowsValue.isFailure()) return rowsValue;

    if (!(
      stream.read() instanceof DelimToken delimToken
      && delimToken.ch() == '/'
    )) return EXPECTED_SLASH;
    
    CSSValue columnsValue = templateTracksParser.parse(stream);
    if (columnsValue.isFailure()) return columnsValue;

    CSSValue autoFlow = GridAutoFlowValue.create(
      GridAutoFlowDirection.ROW, isDense);

    return GridValue.create(
      null, columnsValue, null,
      rowsValue, null, autoFlow);
  }

  private CSSValue parseAutoFlowOrDense(CSSTokenStream stream) throws IOException {
    CSSValue anyOrderResult = PropertyValueParserUtil.parseAnyOrder(stream,
      stream2 -> stream2.read() instanceof IdentToken identToken
        && identToken.value().equals("auto-flow") ? CSSValue.AUTO : EXPECTED_AUTO_FLOW,
      stream2 -> stream2.read() instanceof IdentToken identToken
        && identToken.value().equals("dense") ? CSSValue.AUTO : EXPECTED_DENSE);
    if (anyOrderResult.isFailure()) return anyOrderResult;
    CSSValue[] result = ((AnyOrderResult) anyOrderResult).values();
    if (result[0] == null) return EXPECTED_AUTO_FLOW;
    return result[1];
  }
  
}
