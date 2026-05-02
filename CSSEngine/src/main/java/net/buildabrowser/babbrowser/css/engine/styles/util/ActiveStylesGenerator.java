package net.buildabrowser.babbrowser.css.engine.styles.util;

import java.util.Collection;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleRule;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSDeferred;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSVarValue;
import net.buildabrowser.babbrowser.cssbase.property.CustomPropertyParser;
import net.buildabrowser.babbrowser.cssbase.property.DeclarationParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public final class ActiveStylesGenerator {
  
  private ActiveStylesGenerator() {}

  public static ActiveStyles generateActiveStyles(
    Collection<WeightedStyleRule> styleRules,
    ActiveStyles parentStyles
  ) {
    ActiveStyles activeStyles = ActiveStyles.create(parentStyles);
    for (WeightedStyleRule styleRule: styleRules) {
      addToActiveStyles(activeStyles, styleRule.rule());
    }

    return activeStyles;
  }

  private static void addToActiveStyles(ActiveStyles activeStyles, StyleRule styleRule) {
    for (Declaration declaration: styleRule.declarations()) {
      if (!declaration.name().startsWith("--")) continue;
      parseCustomDeclaration(activeStyles, declaration);
    }
    for (Declaration declaration: styleRule.declarations()) {
      if (declaration.name().startsWith("--")) continue;
      parseDeclaration(declaration, activeStyles);
    }
  }

  private static void parseDeclaration(Declaration declaration, ActiveStyles activeStyles) {
    PropertyValueParser declarationDetails = DeclarationParser.declarationDetails(declaration.name());
    if (declarationDetails == null) return;
    CSSValue declValue = declaration.evaluate();

    if (declValue instanceof CSSDeferred deferredValue) {
      declValue = CommonUtil.rethrow(() -> DeclarationParser.parseDeferredDeclaration(
        declaration.source(), deferredValue, activeStyles));
    }

    switch (declValue) {
      case CSSValue.SpecialCSSValue.INITIAL -> activeStyles.useInitialProperty(declarationDetails.relatedProperty());
      case CSSValue.SpecialCSSValue.INHERIT -> activeStyles.inheritProperty(declarationDetails.relatedProperty());
      case CSSValue.SpecialCSSValue.UNSET -> activeStyles.unsetProperty(declarationDetails.relatedProperty());
      case CSSValue.SpecialCSSValue.INVALID -> activeStyles.unsetProperty(declarationDetails.relatedProperty());
      default -> declarationDetails.updateProperty(declValue, activeStyles);
    }
  }

  private static void parseCustomDeclaration(ActiveStyles activeStyles, Declaration declaration) {
    CSSTokenStream tokenStream = ListCSSTokenStream.createWithSkippedWhitespace(
      declaration.source(), declaration.value());
    boolean isValidCustomPropertyValue = CommonUtil.rethrow(
      () -> CustomPropertyParser.isValidCustomPropertyValue(tokenStream, true));
    if (!isValidCustomPropertyValue) {
      return;
    }
    if (
      declaration.value().size() == 1
      && declaration.value().get(0) instanceof IdentToken identToken
      && identToken.value().equals("initial")
    ) {
      activeStyles.useInitialCustomProperty(declaration.name());
    } else {
      activeStyles.setCustomProperty(declaration.name(), new CSSVarValue(declaration.value()));
    }
  }

}
