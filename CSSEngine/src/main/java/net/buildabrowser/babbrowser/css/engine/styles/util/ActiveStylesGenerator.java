package net.buildabrowser.babbrowser.css.engine.styles.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.background.BackgroundColorParser;
import net.buildabrowser.babbrowser.css.engine.property.border.BorderColorParser;
import net.buildabrowser.babbrowser.css.engine.property.border.BorderShorthandParser;
import net.buildabrowser.babbrowser.css.engine.property.border.BorderSideShorthandParser;
import net.buildabrowser.babbrowser.css.engine.property.border.BorderSizeParser;
import net.buildabrowser.babbrowser.css.engine.property.border.BorderStyleParser;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorBaseParser;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorParser;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayParser;
import net.buildabrowser.babbrowser.css.engine.property.floats.ClearParser;
import net.buildabrowser.babbrowser.css.engine.property.floats.FloatParser;
import net.buildabrowser.babbrowser.css.engine.property.shared.ManySideShorthandParser;
import net.buildabrowser.babbrowser.css.engine.property.size.SizeParser;
import net.buildabrowser.babbrowser.css.engine.property.text.TextWrapModeParser;
import net.buildabrowser.babbrowser.css.engine.property.whitespace.WhitespaceCollapseValueParser;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public final class ActiveStylesGenerator {

  private final static Map<String, PropertyValueParser> PROPERTY_PARSERS = mapOf(
    "color", new ColorParser(),
    "background", new BackgroundColorParser(),
    "background-color", new BackgroundColorParser(),
    "clear", new ClearParser(),
    "float", new FloatParser(),

    "display", new DisplayParser(),

    "width", SizeParser.forNormal(CSSProperty.WIDTH),
    "height", SizeParser.forNormal(CSSProperty.HEIGHT),

    "padding-top", SizeParser.forPadding(CSSProperty.PADDING_TOP),
    "padding-bottom", SizeParser.forPadding(CSSProperty.PADDING_BOTTOM),
    "padding-left", SizeParser.forPadding(CSSProperty.PADDING_LEFT),
    "padding-right", SizeParser.forPadding(CSSProperty.PADDING_RIGHT),
    "padding", new ManySideShorthandParser(new SizeParser(false, false, null)::parseInternal,
      new CSSProperty[] { CSSProperty.PADDING_TOP, CSSProperty.PADDING_RIGHT, CSSProperty.PADDING_BOTTOM, CSSProperty.PADDING_LEFT },
      CSSProperty.PADDING),
    
    "border-top-width", new BorderSizeParser(CSSProperty.BORDER_TOP_WIDTH),
    "border-bottom-width", new BorderSizeParser(CSSProperty.BORDER_BOTTOM_WIDTH),
    "border-left-width", new BorderSizeParser(CSSProperty.BORDER_LEFT_WIDTH),
    "border-right-width", new BorderSizeParser(CSSProperty.BORDER_RIGHT_WIDTH),
    "border-width", new ManySideShorthandParser(new BorderSizeParser(null)::parseInternal,
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
    "border-style", new ManySideShorthandParser(new BorderStyleParser(null)::parseInternal,
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
    "margin", new ManySideShorthandParser(new SizeParser(false, true, null)::parseInternal,
      new CSSProperty[] { CSSProperty.MARGIN_TOP, CSSProperty.MARGIN_RIGHT, CSSProperty.MARGIN_BOTTOM, CSSProperty.MARGIN_LEFT },
      CSSProperty.MARGIN),

    "white-space-collapse", new WhitespaceCollapseValueParser(),
    "text-wrap-mode", new TextWrapModeParser()
  );
  
  private ActiveStylesGenerator() {}

  public static ActiveStyles generateActiveStyles(Set<WeightedStyleRule> styleRules, ActiveStyles parentStyles) {
    ActiveStyles activeStyles = ActiveStyles.create(parentStyles);
    for (WeightedStyleRule styleRule: styleRules) {
      addToActiveStyles(activeStyles, styleRule.rule());
    }

    return activeStyles;
  }

  private static void addToActiveStyles(ActiveStyles activeStyles, StyleRule styleRule) {
    for (Declaration declaration: styleRule.declarations()) {
      parseDeclaration(declaration, activeStyles);
    }
  }

  private static void parseDeclaration(Declaration declaration, ActiveStyles activeStyles) {
    PropertyValueParser parser = PROPERTY_PARSERS.get(declaration.name());
    if (parser == null) return;
    if (parser.relatedProperty() == null) {
      throw new UnsupportedOperationException("Parser does not have a related property!");
    }

    if (
      declaration.value().size() == 1
      && declaration.value().get(0) instanceof IdentToken identToken
    ) {
      if (identToken.value().equals("initial")) {
        activeStyles.useInitialProperty(parser.relatedProperty());
        return;
      } else if (identToken.value().equals("inherit")) {
        activeStyles.inheritProperty(parser.relatedProperty());
        return;
      } else if (identToken.value().equals("unset")) {
        activeStyles.unsetProperty(parser.relatedProperty());
        return;
      }

      // TODO: Support revert keyword
    }

    // TODO: Do any cases preserve whitespace?
    SeekableCSSTokenStream tokenStream = ListCSSTokenStream.createWithSkippedWhitespace(declaration.value());
    try {
      parser.parse(tokenStream, activeStyles);
    } catch (IOException e) {
      e.printStackTrace();
    }
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
