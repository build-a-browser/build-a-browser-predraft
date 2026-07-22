package net.buildabrowser.babbrowser.cssbase.property.whitespace;

import java.io.IOException;
import java.util.Map;

import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.MutablePropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.AnyOrderResult;
import net.buildabrowser.babbrowser.cssbase.property.text.TextWrapModeParser;
import net.buildabrowser.babbrowser.cssbase.property.text.TextWrapModeValue;

public class WhiteSpaceParser implements PropertyValueParser {

  private static final WhiteSpaceCompositeValue NORMAL = WhiteSpaceCompositeValue.create(
    WhiteSpaceCollapseValue.COLLAPSE,
    TextWrapModeValue.WRAP,
    CSSValue.NONE);
  private static final WhiteSpaceCompositeValue PRE = WhiteSpaceCompositeValue.create(
    WhiteSpaceCollapseValue.PRESERVE,
    TextWrapModeValue.NOWRAP,
    CSSValue.NONE);
  private static final WhiteSpaceCompositeValue PRE_WRAP = WhiteSpaceCompositeValue.create(
    WhiteSpaceCollapseValue.PRESERVE,
    TextWrapModeValue.WRAP,
    CSSValue.NONE);
  private static final WhiteSpaceCompositeValue PRE_LINE = WhiteSpaceCompositeValue.create(
    WhiteSpaceCollapseValue.PRESERVE_BREAKS,
    TextWrapModeValue.WRAP,
    CSSValue.NONE);

  private static final Map<String, CSSValue> WHITE_SPACE_VALUES = Map.of(
    "normal", NORMAL,
    "pre", PRE,
    "pre-wrap", PRE_WRAP,
    "pre-line", PRE_LINE
  );

  private final WhiteSpaceCollapseParser whiteSpaceCollapseParser
    = new WhiteSpaceCollapseParser();
  private final TextWrapModeParser textWrapModeParser
    = new TextWrapModeParser();
  private final WhiteSpaceTrimParser whiteSpaceTrimParser
    = new WhiteSpaceTrimParser();

  private final PropertyValueParser[] parsers = new PropertyValueParser[] {
    whiteSpaceCollapseParser,
    textWrapModeParser,
    whiteSpaceTrimParser };

  @Override
  public CSSValue parse(SeekableCSSTokenStream stream) throws IOException {
    int initialPos = stream.position();
    
    CSSValue shortHandValue = PropertyValueParserUtil.parseIdentMap(
      stream, WHITE_SPACE_VALUES);
    if (!shortHandValue.isFailure()) return shortHandValue;

    stream.seek(initialPos);

    CSSValue anyOrderResult = PropertyValueParserUtil.parseAnyOrder(stream, parsers);
    if (anyOrderResult.isFailure()) return anyOrderResult;

    CSSValue[] anyOrderValues = ((AnyOrderResult) anyOrderResult).values();
    CSSValue collapseValue = anyOrderValues[0] == null ?
      CSSProperty.WHITE_SPACE_COLLAPSE.initial() :
      anyOrderValues[0];
    CSSValue wrapValue = anyOrderValues[1] == null ?
      CSSProperty.TEXT_WRAP_MODE.initial() :
      anyOrderValues[1];
    CSSValue trimValue = anyOrderValues[2] == null ?
      CSSProperty.WHITE_SPACE_TRIM.initial() :
      anyOrderValues[2];
    
    return new WhiteSpaceCompositeValue(
      collapseValue, wrapValue, trimValue);
  }

  @Override
  public CSSProperty relatedProperty() {
    return CSSProperty.WHITE_SPACE;
  }

  @Override
  public void updateProperty(
    CSSValue result, MutablePropertyContainer propertySetter
  ) {
    WhiteSpaceCompositeValue compositeValue = (WhiteSpaceCompositeValue) result;
    propertySetter.setProperty(
      CSSProperty.WHITE_SPACE_COLLAPSE, compositeValue.whiteSpaceCollapse());
    propertySetter.setProperty(
      CSSProperty.TEXT_WRAP_MODE, compositeValue.textWrapMode());
    propertySetter.setProperty(
      CSSProperty.WHITE_SPACE_TRIM, compositeValue.whiteSpaceTrim());
  }
  
}
