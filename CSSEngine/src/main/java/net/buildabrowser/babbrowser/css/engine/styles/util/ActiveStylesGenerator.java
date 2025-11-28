package net.buildabrowser.babbrowser.css.engine.styles.util;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorParser;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public final class ActiveStylesGenerator {

  private final static Map<String, PropertyValueParser> PROPERTY_PARSERS = Map.of(
    "color", new ColorParser()
  );
  
  private ActiveStylesGenerator() {}

  public static ActiveStyles generateActiveStyles(Set<StyleRule> styleRules) {
    ActiveStyles activeStyles = ActiveStyles.create();
    for (StyleRule styleRule: styleRules) {
      addToActiveStyles(activeStyles, styleRule);
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

    SeekableCSSTokenStream tokenStream = CSSTokenStream.create(declaration.value());
    try {
      parser.parse(tokenStream, activeStyles);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
