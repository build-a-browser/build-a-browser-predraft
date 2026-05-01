package net.buildabrowser.babbrowser.cssbase.property.flex;

import java.io.IOException;

import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.AnyOrderResult;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.NumberToken;

public class FlexParser implements PropertyValueParser {

  private final FlexGrowParser flexGrowParser = new FlexGrowParser();
  private final FlexShrinkParser flexShrinkParser = new FlexShrinkParser();
  private final FlexBasisParser flexBasisParser = new FlexBasisParser();

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    if (
      stream.peek() instanceof IdentToken identToken
      && identToken.value().equals("none")
    ) {
      return FlexValue.create(
        FlexGrowValue.create(0),
        FlexShrinkValue.create(0),
        CSSValue.AUTO);
    }

    CSSValue anyOrderValue = PropertyValueParserUtil.parseAnyOrder(stream,
      this::parseGrowShrink, flexBasisParser);
    if (anyOrderValue.isFailure()) return anyOrderValue;
    CSSValue[] innerValues = ((AnyOrderResult) anyOrderValue).values();

    CSSValue basis = innerValues[1] == null ? CSSValue.AUTO : innerValues[1];
    return innerValues[0] instanceof FlexValue innerFlexValue ?
      FlexValue.create(
        innerFlexValue.flexGrow(),
        innerFlexValue.flexShrink(),
        basis) :
      FlexValue.create(
        FlexGrowValue.create(1),
        FlexShrinkValue.create(1),
        basis);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.FLEX;
  }

  @Override
  public void updateProperty(CSSValue result, PropertyContainer propertySetter) {
    FlexValue flexValue = (FlexValue) result;
    propertySetter.setProperty(CSSProperty.FLEX_GROW, flexValue.flexGrow());
    propertySetter.setProperty(CSSProperty.FLEX_SHRINK, flexValue.flexShrink());
    propertySetter.setProperty(CSSProperty.FLEX_BASIS, flexValue.flexBasis());
  }

  private CSSValue parseGrowShrink(SeekableCSSTokenStream stream) throws IOException {
    CSSValue flexGrowValue = flexGrowParser.parse(stream);
    if (flexGrowValue.isFailure()) return flexGrowValue;

    CSSValue flexShrinkValue = stream.peek() instanceof NumberToken ?
      flexShrinkParser.parse(stream) :
      FlexShrinkValue.create(1);
    if (flexShrinkValue.isFailure()) return flexShrinkValue;

    return new FlexValue(
      (FlexGrowValue) flexGrowValue,
      (FlexShrinkValue) flexShrinkValue,
      null);
  }
  
}
