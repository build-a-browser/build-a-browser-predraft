package net.buildabrowser.babbrowser.css.engine.styles.util;

import java.util.Collection;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.DeclarationParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;

public final class ActiveStylesGenerator {
  
  private ActiveStylesGenerator() {}

  public static ActiveStyles generateActiveStyles(Collection<WeightedStyleRule> styleRules, ActiveStyles parentStyles) {
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
    PropertyValueParser declarationDetails = DeclarationParser.declarationDetails(declaration.name());
    if (declarationDetails == null) return;
    CSSValue declValue = declaration.evaluate();

    switch (declValue) {
      case CSSValue.SpecialCSSValue.INITIAL -> activeStyles.useInitialProperty(declarationDetails.relatedProperty());
      case CSSValue.SpecialCSSValue.INHERIT -> activeStyles.inheritProperty(declarationDetails.relatedProperty());
      case CSSValue.SpecialCSSValue.UNSET -> activeStyles.unsetProperty(declarationDetails.relatedProperty());
      case CSSValue.SpecialCSSValue.INVALID -> {}
      default -> declarationDetails.updateProperty(declValue, (prop, val) -> activeStyles.setProperty(prop, val));
    }
  }

}
