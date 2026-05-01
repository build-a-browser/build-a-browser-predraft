package net.buildabrowser.babbrowser.cssbase.property;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSDeferred;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSVarValue;
import net.buildabrowser.babbrowser.cssbase.property.align.GapParser;
import net.buildabrowser.babbrowser.cssbase.property.align.GapShorthandParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundAttachmentParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundClipParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundColorParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundImageParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundOriginParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundPositionParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundRepeatParser;
import net.buildabrowser.babbrowser.cssbase.property.background.BackgroundSizeParser;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderColorParser;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderShorthandParser;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderSideShorthandParser;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderSizeParser;
import net.buildabrowser.babbrowser.cssbase.property.border.BorderStyleParser;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorBaseParser;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorParser;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayParser;
import net.buildabrowser.babbrowser.cssbase.property.display.OrderParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignContentParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignItemsParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.AlignSelfParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexBasisParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexDirectionParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexFlowParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexGrowParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexShrinkParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.FlexWrapParser;
import net.buildabrowser.babbrowser.cssbase.property.flex.JustifyContentParser;
import net.buildabrowser.babbrowser.cssbase.property.floats.ClearParser;
import net.buildabrowser.babbrowser.cssbase.property.floats.FloatParser;
import net.buildabrowser.babbrowser.cssbase.property.font.FontFamilyParser;
import net.buildabrowser.babbrowser.cssbase.property.font.FontShorthandParser;
import net.buildabrowser.babbrowser.cssbase.property.font.FontSizeParser;
import net.buildabrowser.babbrowser.cssbase.property.font.FontWeightParser;
import net.buildabrowser.babbrowser.cssbase.property.overflow.OverflowParser;
import net.buildabrowser.babbrowser.cssbase.property.overflow.OverflowShorthandParser;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionParser;
import net.buildabrowser.babbrowser.cssbase.property.position.ZIndexParser;
import net.buildabrowser.babbrowser.cssbase.property.shared.ManySideShorthandParser;
import net.buildabrowser.babbrowser.cssbase.property.size.BoxSizingParser;
import net.buildabrowser.babbrowser.cssbase.property.size.SizeParser;
import net.buildabrowser.babbrowser.cssbase.property.text.LineHeightParser;
import net.buildabrowser.babbrowser.cssbase.property.text.TextAlignParser;
import net.buildabrowser.babbrowser.cssbase.property.text.TextWrapModeParser;
import net.buildabrowser.babbrowser.cssbase.property.whitespace.WhitespaceCollapseValueParser;
import net.buildabrowser.babbrowser.cssbase.tokens.EOFToken;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public final class DeclarationParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeclarationParser.class);

  // TODO: A number of these need CalcParser added. Things handled by SizeParser get this free.
  private final static Map<String, PropertyValueParser> PROPERTY_PARSERS = mapOf(
    "color", new ColorParser(),
    
    "background-color", new BackgroundColorParser(),
    "background-image", new BackgroundImageParser(),
    "background-repeat", new BackgroundRepeatParser(),
    "background-attachment", new BackgroundAttachmentParser(),
    "background-position", new BackgroundPositionParser(),
    "background-clip", new BackgroundClipParser(),
    "background-origin", new BackgroundOriginParser(),
    "background-size", new BackgroundSizeParser(),
    "background", new BackgroundColorParser(),

    "clear", new ClearParser(),
    "float", new FloatParser(),

    "display", new DisplayParser(),

    "width", SizeParser.forNormal(CSSProperty.WIDTH),
    "min-width", SizeParser.forMin(CSSProperty.MIN_WIDTH),
    "max-width", SizeParser.forMin(CSSProperty.MAX_WIDTH),
    
    "height", SizeParser.forNormal(CSSProperty.HEIGHT),
    "min-height", SizeParser.forMin(CSSProperty.MIN_HEIGHT),
    "max-height", SizeParser.forMin(CSSProperty.MAX_HEIGHT),
    
    "box-sizing", new BoxSizingParser(),

    "font-family", new FontFamilyParser(),
    "font-weight", new FontWeightParser(),
    "font-size", new FontSizeParser(),
    "font", new FontShorthandParser(),

    "padding-top", SizeParser.forPadding(CSSProperty.PADDING_TOP),
    "padding-bottom", SizeParser.forPadding(CSSProperty.PADDING_BOTTOM),
    "padding-left", SizeParser.forPadding(CSSProperty.PADDING_LEFT),
    "padding-right", SizeParser.forPadding(CSSProperty.PADDING_RIGHT),
    "padding", new ManySideShorthandParser(new SizeParser(false, false, null),
      new CSSProperty[] { CSSProperty.PADDING_TOP, CSSProperty.PADDING_RIGHT, CSSProperty.PADDING_BOTTOM, CSSProperty.PADDING_LEFT },
      CSSProperty.PADDING),
    
    "border-top-width", new BorderSizeParser(CSSProperty.BORDER_TOP_WIDTH),
    "border-bottom-width", new BorderSizeParser(CSSProperty.BORDER_BOTTOM_WIDTH),
    "border-left-width", new BorderSizeParser(CSSProperty.BORDER_LEFT_WIDTH),
    "border-right-width", new BorderSizeParser(CSSProperty.BORDER_RIGHT_WIDTH),
    "border-width", new ManySideShorthandParser(new BorderSizeParser(null),
      new CSSProperty[] { CSSProperty.BORDER_TOP_WIDTH, CSSProperty.BORDER_RIGHT_WIDTH, CSSProperty.BORDER_BOTTOM_WIDTH, CSSProperty.BORDER_LEFT_WIDTH },
      CSSProperty.BORDER_WIDTH),

    "border-top-color", new BorderColorParser(CSSProperty.BORDER_TOP_COLOR),
    "border-bottom-color", new BorderColorParser(CSSProperty.BORDER_BOTTOM_COLOR),
    "border-left-color", new BorderColorParser(CSSProperty.BORDER_LEFT_COLOR),
    "border-right-color", new BorderColorParser(CSSProperty.BORDER_RIGHT_COLOR),
    "border-color", new ManySideShorthandParser(new ColorBaseParser(),
      new CSSProperty[] { CSSProperty.BORDER_TOP_COLOR, CSSProperty.BORDER_RIGHT_COLOR, CSSProperty.BORDER_BOTTOM_COLOR, CSSProperty.BORDER_LEFT_COLOR },
      CSSProperty.BORDER_COLOR),

    "border-top-style", new BorderStyleParser(CSSProperty.BORDER_TOP_STYLE),
    "border-bottom-style", new BorderStyleParser(CSSProperty.BORDER_BOTTOM_STYLE),
    "border-left-style", new BorderStyleParser(CSSProperty.BORDER_LEFT_STYLE),
    "border-right-style", new BorderStyleParser(CSSProperty.BORDER_RIGHT_STYLE),
    "border-style", new ManySideShorthandParser(new BorderStyleParser(null),
      new CSSProperty[] { CSSProperty.BORDER_TOP_STYLE, CSSProperty.BORDER_RIGHT_STYLE, CSSProperty.BORDER_BOTTOM_STYLE, CSSProperty.BORDER_LEFT_STYLE },
      CSSProperty.BORDER_STYLE),

    "border-top", new BorderSideShorthandParser(CSSProperty.BORDER_TOP, CSSProperty.BORDER_TOP_WIDTH, CSSProperty.BORDER_TOP_COLOR, CSSProperty.BORDER_TOP_STYLE),
    "border-bottom", new BorderSideShorthandParser(CSSProperty.BORDER_BOTTOM, CSSProperty.BORDER_BOTTOM_WIDTH, CSSProperty.BORDER_BOTTOM_COLOR, CSSProperty.BORDER_BOTTOM_STYLE),
    "border-left", new BorderSideShorthandParser(CSSProperty.BORDER_LEFT, CSSProperty.BORDER_LEFT_WIDTH, CSSProperty.BORDER_LEFT_COLOR, CSSProperty.BORDER_LEFT_STYLE),
    "border-right", new BorderSideShorthandParser(CSSProperty.BORDER_RIGHT, CSSProperty.BORDER_RIGHT_WIDTH, CSSProperty.BORDER_RIGHT_COLOR, CSSProperty.BORDER_RIGHT_STYLE),
    "border", new BorderShorthandParser(),

    "margin-top", SizeParser.forMargin(CSSProperty.MARGIN_TOP),
    "margin-bottom", SizeParser.forMargin(CSSProperty.MARGIN_BOTTOM),
    "margin-left", SizeParser.forMargin(CSSProperty.MARGIN_LEFT),
    "margin-right", SizeParser.forMargin(CSSProperty.MARGIN_RIGHT),
    "margin", new ManySideShorthandParser(new SizeParser(false, true, null),
      new CSSProperty[] { CSSProperty.MARGIN_TOP, CSSProperty.MARGIN_RIGHT, CSSProperty.MARGIN_BOTTOM, CSSProperty.MARGIN_LEFT },
      CSSProperty.MARGIN),
    
    "top", SizeParser.forInset(CSSProperty.TOP),
    "bottom", SizeParser.forInset(CSSProperty.BOTTOM),
    "left", SizeParser.forInset(CSSProperty.LEFT),
    "right", SizeParser.forInset(CSSProperty.RIGHT),

    "position", new PositionParser(),
    "z-index", new ZIndexParser(),

    "white-space-collapse", new WhitespaceCollapseValueParser(),
    "text-wrap-mode", new TextWrapModeParser(),
    "line-height", new LineHeightParser(),
    "text-align", new TextAlignParser(),

    "order", new OrderParser(),

    "flex-direction", new FlexDirectionParser(),
    "flex-wrap", new FlexWrapParser(),
    "flex-flow", new FlexFlowParser(),
    "flex", new FlexParser(),
    "flex-grow", new FlexGrowParser(),
    "flex-shrink", new FlexShrinkParser(),
    "flex-basis", new FlexBasisParser(),
    "justify-content", new JustifyContentParser(),
    "align-items", new AlignItemsParser(),
    "align-self", new AlignSelfParser(),
    "align-content", new AlignContentParser(),

    "row-gap", new GapParser(CSSProperty.ROW_GAP),
    "column-gap", new GapParser(CSSProperty.COLUMN_GAP),
    "gap", new GapShorthandParser(),

    "overflow-x", new OverflowParser(CSSProperty.OVERFLOW_X),
    "overflow-y", new OverflowParser(CSSProperty.OVERFLOW_Y),
    // TODO: Proper implementation
    "overflow-block", new OverflowParser(CSSProperty.OVERFLOW_X),
    "overflow-inline", new OverflowParser(CSSProperty.OVERFLOW_Y),
    "overflow", new OverflowShorthandParser()
  );

  public static boolean isKnownDeclarationName(String declName) {
    return PROPERTY_PARSERS.containsKey(declName.toLowerCase());
  }

  public static CSSValue parseDeclaration(Declaration declaration) {
    PropertyValueParser parser = PROPERTY_PARSERS.get(declaration.name().toLowerCase());
    if (parser == null) return CSSValue.SpecialCSSValue.INVALID;
    if (parser.relatedProperty() == null) {
      throw new UnsupportedOperationException("Parser does not have a related property!");
    }

    if (
      declaration.value().size() == 1
      && declaration.value().get(0) instanceof IdentToken identToken
    ) {
      if (identToken.value().equals("initial")) {
        return CSSValue.SpecialCSSValue.INITIAL;
      } else if (identToken.value().equals("inherit")) {
        return CSSValue.SpecialCSSValue.INHERIT;
      } else if (identToken.value().equals("unset")) {
        return CSSValue.SpecialCSSValue.UNSET;
      }

      // TODO: Support revert keyword
    }

    Boolean shouldDefer = CustomPropertyParser.hasVarReferences(declaration.value());
    if (shouldDefer == null) return CSSValue.SpecialCSSValue.INVALID;
    if (shouldDefer) return new CSSDeferred(declaration, parser);

    // TODO: Do any cases preserve whitespace?
    SeekableCSSTokenStream tokenStream = ListCSSTokenStream.createWithSkippedWhitespace(declaration.value());
    try {
      CSSValue result = parser.parse(tokenStream);
      if (
        !result.isFailure()
        && tokenStream.peek() instanceof EOFToken
      ) {
        return result;
      }
    } catch (IOException e) {
      LOGGER.error("Could not parse the declaration!", e);
    }

    return CSSValue.SpecialCSSValue.INVALID;
  }
  
  public static PropertyValueParser declarationDetails(String declName) {
    PropertyValueParser parser = PROPERTY_PARSERS.get(declName);
    if (parser == null) return null;
    if (parser.relatedProperty() == null) {
      throw new UnsupportedOperationException("Parser does not have a related property!");
    }

    return parser;
  }

  public static CSSValue parseDeferredDeclaration(CSSDeferred deferredValue, PropertyContainer refContainer) {
    CSSValue resolvedValue = CustomPropertyParser.resolveVarValues(deferredValue.value(), refContainer);
    if (resolvedValue == null) return CSSValue.SpecialCSSValue.INVALID;
    if (resolvedValue.isFailure()) return CSSValue.SpecialCSSValue.INVALID;
    List<Token> resolvedTokens = ((CSSVarValue) resolvedValue).propertyTokens();
    SeekableCSSTokenStream tokenStream = ListCSSTokenStream.createWithSkippedWhitespace(resolvedTokens);
    try {
      CSSValue result = deferredValue.parser().parse(tokenStream);
      if (
        !result.isFailure()
        && tokenStream.peek() instanceof EOFToken
      ) {
        return result;
      }
    } catch (IOException e) {
      LOGGER.error("Could not parse the declaration!", e);
    }

    return CSSValue.SpecialCSSValue.INVALID;
  }

  @SuppressWarnings("unchecked")
  private static <T, U> Map<T, U> mapOf(Object... values) {
    Map<T, U> map = new HashMap<>();
    for (int i = 0; i < values.length; i += 2) {
      map.put((T) values[i], (U) values[i + 1]);
    }

    return Map.copyOf(map);
  }

}
