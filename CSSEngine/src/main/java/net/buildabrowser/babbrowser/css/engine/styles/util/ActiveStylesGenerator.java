package net.buildabrowser.babbrowser.css.engine.styles.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.background.BackgroundColorParser;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorParser;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayParser;
import net.buildabrowser.babbrowser.css.engine.property.floats.ClearParser;
import net.buildabrowser.babbrowser.css.engine.property.floats.FloatParser;
import net.buildabrowser.babbrowser.css.engine.property.size.SizeParser;
import net.buildabrowser.babbrowser.css.engine.property.text.TextWrapModeParser;
import net.buildabrowser.babbrowser.css.engine.property.whitespace.WhitespaceCollapseValueParser;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;
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

    SeekableCSSTokenStream tokenStream = CSSTokenStream.create(declaration.value());
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
