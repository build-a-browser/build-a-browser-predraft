package net.buildabrowser.babbrowser.css.engine.styles.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.buildabrowser.babbrowser.css.engine.property.PropertyValueParser;
import net.buildabrowser.babbrowser.css.engine.property.background.BackgroundColorParser;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorParser;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayParser;
import net.buildabrowser.babbrowser.css.engine.property.floats.ClearParser;
import net.buildabrowser.babbrowser.css.engine.property.floats.FloatParser;
import net.buildabrowser.babbrowser.css.engine.property.size.SizeParser;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles.SizingUnit;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.CSSParser.SeekableCSSTokenStream;

public final class ActiveStylesGenerator {

  private final static Map<String, PropertyValueParser> PROPERTY_PARSERS = mapOf(
    "color", new ColorParser(),
    "background", new BackgroundColorParser(),
    "background-color", new BackgroundColorParser(),
    "clear", new ClearParser(),
    "float", new FloatParser(),

    "display", new DisplayParser(),

    "margin-top", SizeParser.forMargin(SizingUnit.MARGIN_TOP),
    "margin-right", SizeParser.forMargin(SizingUnit.MARGIN_RIGHT),
    "margin-bottom", SizeParser.forMargin(SizingUnit.MARGIN_BOTTOM),
    "margin-left", SizeParser.forMargin(SizingUnit.MARGIN_LEFT),

    "padding-top", SizeParser.forPadding(SizingUnit.PADDING_TOP),
    "padding-right", SizeParser.forPadding(SizingUnit.PADDING_RIGHT),
    "padding-bottom", SizeParser.forPadding(SizingUnit.PADDING_BOTTOM),
    "padding-left", SizeParser.forPadding(SizingUnit.PADDING_LEFT),

    "top", SizeParser.forPosition(SizingUnit.TOP),
    "right", SizeParser.forPosition(SizingUnit.RIGHT),
    "bottom", SizeParser.forPosition(SizingUnit.BOTTOM),
    "left", SizeParser.forPosition(SizingUnit.LEFT),

    "width", SizeParser.forNormal(SizingUnit.WIDTH),
    "min-width", SizeParser.forMin(SizingUnit.MIN_WIDTH),
    "max-width", SizeParser.forMax(SizingUnit.MAX_WIDTH),

    "height", SizeParser.forNormal(SizingUnit.HEIGHT),
    "min-height", SizeParser.forMin(SizingUnit.MIN_HEIGHT),
    "max-height", SizeParser.forMax(SizingUnit.MAX_HEIGHT)
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

  @SuppressWarnings("unchecked")
  private static <T, U> Map<T, U> mapOf(Object... values) {
    Map<T, U> map = new HashMap<>();
    for (int i = 0; i < values.length; i += 2) {
      map.put((T) values[i], (U) values[i + 1]);
    }

    return Map.copyOf(map);
  }

}
