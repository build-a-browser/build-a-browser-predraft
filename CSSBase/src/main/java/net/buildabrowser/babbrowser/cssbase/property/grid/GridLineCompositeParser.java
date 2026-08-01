package net.buildabrowser.babbrowser.cssbase.property.grid;

import java.io.IOException;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.MutablePropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.grid.GridLineValue.CustomIdentValue;

public class GridLineCompositeParser implements PropertyValueParser {

  private final CSSProperty relatedProperty;
  private final CSSProperty startProperty;
  private final CSSProperty endProperty;

  public GridLineCompositeParser(
    CSSProperty relatedProperty,
    CSSProperty startProperty,
    CSSProperty endProperty
  ) {
    this.relatedProperty = relatedProperty;
    this.startProperty = startProperty;
    this.endProperty = endProperty;
  }

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    CSSValue firstValue = GridLineParser.parseLine(stream);
    if (firstValue.isFailure()) return firstValue;
    CSSValue secondValue = GridLineParser.maybeParseNextLine(stream);
    if (secondValue != null && secondValue.isFailure()) return secondValue;

    secondValue =
      secondValue != null ? secondValue :
      firstValue instanceof CustomIdentValue ? firstValue :
      CSSValue.AUTO;
    
    return new GridLineCompositeValue(List.of(firstValue, secondValue));
  }

  @Override
  public CSSProperty relatedProperty() {
    return this.relatedProperty;
  }

  @Override
  public void updateProperty(
    CSSValue result, MutablePropertyContainer propertySetter
  ) {
    List<CSSValue> lines = ((GridLineCompositeValue) result).gridLines();
    propertySetter.setProperty(startProperty, lines.get(0));
    propertySetter.setProperty(endProperty, lines.get(1));
  }

}
