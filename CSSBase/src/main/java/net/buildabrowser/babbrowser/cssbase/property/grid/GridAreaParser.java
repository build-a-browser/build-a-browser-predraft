package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.MutablePropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridLineValue.CustomIdentValue;

public class GridAreaParser implements PropertyValueParser {

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    CSSValue gridRowStart = GridLineParser.parseLine(stream);
    if (gridRowStart.isFailure()) return gridRowStart;
    
    CSSValue gridColumnStart = GridLineParser.maybeParseNextLine(stream);
    if (gridColumnStart != null && gridColumnStart.isFailure()) return gridColumnStart;
    gridColumnStart =
      gridColumnStart != null ? gridColumnStart :
      gridRowStart instanceof CustomIdentValue ? gridRowStart :
      CSSValue.AUTO;

    CSSValue gridRowEnd = GridLineParser.maybeParseNextLine(stream);
    if (gridRowEnd != null && gridRowEnd.isFailure()) return gridRowEnd;
    gridRowEnd =
      gridRowEnd != null ? gridRowEnd :
      gridRowStart instanceof CustomIdentValue ? gridRowStart :
      CSSValue.AUTO;

    CSSValue gridColumnEnd = GridLineParser.maybeParseNextLine(stream);
    if (gridColumnEnd != null && gridColumnEnd.isFailure()) return gridColumnEnd;
    gridColumnEnd =
      gridColumnEnd != null ? gridColumnEnd :
      gridColumnStart instanceof CustomIdentValue ? gridColumnStart :
      CSSValue.AUTO;

    if (
      gridColumnStart == null
      && gridRowStart instanceof CustomIdentValue
    ) {
      gridColumnEnd = gridRowEnd = gridColumnStart = gridRowStart;
    }
    
    return new GridLineCompositeValue(List.of(
      gridRowStart, gridColumnStart, gridRowEnd, gridColumnEnd));
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.GRID_AREA;
  }

  @Override
  public void updateProperty(
    CSSValue result, MutablePropertyContainer propertySetter
  ) {
    List<CSSValue> lines = ((GridLineCompositeValue) result).gridLines();
    propertySetter.setProperty(CSSProperty.GRID_ROW_START, lines.get(0));
    propertySetter.setProperty(CSSProperty.GRID_COLUMN_START, lines.get(1));
    propertySetter.setProperty(CSSProperty.GRID_ROW_END, lines.get(2));
    propertySetter.setProperty(CSSProperty.GRID_COLUMN_END, lines.get(3));
  }

}