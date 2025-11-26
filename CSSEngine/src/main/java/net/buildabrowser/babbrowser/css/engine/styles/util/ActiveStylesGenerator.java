package net.buildabrowser.babbrowser.css.engine.styles.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;
import net.buildabrowser.babbrowser.cssbase.tokens.Token;

public final class ActiveStylesGenerator {

  private static final Map<String, Integer> COLOR_MAP = Map.of(
    "red", 0xFFFF0000,
    "green", 0xFF00FF00,
    "blue", 0xFF0000FF
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
    // TODO: Other types
    for (Declaration declaration: styleRule.declarations()) {
      switch (declaration.name()) {
        case "color" -> addColorToActiveStyles(activeStyles, declaration.value());
        default -> {}
      }
    }
  }

  private static void addColorToActiveStyles(ActiveStyles activeStyles, List<Token> value) {
    // TODO: More robust parsing
    IdentToken identToken = (IdentToken) value.get(0);
    Integer color = COLOR_MAP.get(identToken.value());
    if (color != null) {
      activeStyles.setTextColor(color);
    }
  }

}
