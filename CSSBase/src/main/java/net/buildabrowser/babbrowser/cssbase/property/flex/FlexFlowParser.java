package net.buildabrowser.babbrowser.cssbase.property.flex;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.MutablePropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.AnyOrderResult;

public class FlexFlowParser implements PropertyValueParser {

  private final FlexDirectionParser flexDirectionParser = new FlexDirectionParser();
  private final FlexWrapParser flexWrapParser = new FlexWrapParser();

  @Override
  public CSSValue parse(CSSTokenStream stream) throws IOException {
    CSSValue result = PropertyValueParserUtil.parseAnyOrder(stream,
      flexDirectionParser, flexWrapParser);
    if (
      result.isFailure()
      || !(result instanceof AnyOrderResult orderResult)
    ) return result;

    return FlexFlowValue.create(
      (FlexDirectionValue) orderResult.values()[0],
      (FlexWrapValue) orderResult.values()[1]);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.FLEX_FLOW;
  }

  @Override
  public void updateProperty(CSSValue result, MutablePropertyContainer propertySetter) {
    FlexFlowValue flexFlow = (FlexFlowValue) result;
    if (flexFlow.direction() != null) {
      propertySetter.setProperty(CSSProperty.FLEX_DIRECTION, flexFlow.direction());
    }
    if (flexFlow.wrap() != null) {
      propertySetter.setProperty(CSSProperty.FLEX_WRAP, flexFlow.wrap());
    }
  }
  
}
