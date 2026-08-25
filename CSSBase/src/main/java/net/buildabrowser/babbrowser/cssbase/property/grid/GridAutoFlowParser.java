package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridAutoFlowValue.GridAutoFlowDirection;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public class GridAutoFlowParser implements PropertyValueParser {

  private static final CSSFailure EXPECTED_FLOW_DIRECTION = new CSSFailure(
    "Expected \"row\" or \"column\" keyword!");

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    boolean isDense = false;
    if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("dense")
    ) {
      stream.read();
      isDense = true;
    }

    GridAutoFlowDirection direction = GridAutoFlowDirection.ROW;
    if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("row")
    ) {
      stream.read();
    } else if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("column")
    ) {
      stream.read();
      direction = GridAutoFlowDirection.COLUMN;
    } else if (!isDense) {
      return EXPECTED_FLOW_DIRECTION;
    }

    if (
      !isDense
      && stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("dense")
    ) {
      stream.read();
      isDense = true;
    }

    return GridAutoFlowValue.create(direction, isDense);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.GRID_AUTO_FLOW;
  }
  
}
