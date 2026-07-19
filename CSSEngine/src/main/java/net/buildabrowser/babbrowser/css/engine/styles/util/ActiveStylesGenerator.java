package net.buildabrowser.babbrowser.css.engine.styles.util;

import java.util.Collection;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.parser.CSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSDeferred;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSVarValue;
import net.buildabrowser.babbrowser.cssbase.property.CustomPropertyParser;
import net.buildabrowser.babbrowser.cssbase.property.DeclarationParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public final class ActiveStylesGenerator {
  
  private ActiveStylesGenerator() {}

  public static ActiveStyles generateActiveStyles(
    Collection<WeightedStyleRule> styleRules,
    PropertyContainer parentProperties
  ) {
    ActiveStyles activeStyles = ActiveStyles.create();
    PropertyContainer asPropertyView = ActiveStyles.parentStyles(parentProperties, activeStyles);
    addCustomDeclarations(styleRules, activeStyles, false);
    addCustomDeclarations(styleRules, activeStyles, true);
    addNormalDeclarations(styleRules, activeStyles, asPropertyView, false);
    addNormalDeclarations(styleRules, activeStyles, asPropertyView, true);
    activeStyles.freeze();

    return activeStyles;
  }

  private static void addNormalDeclarations(
    Collection<WeightedStyleRule> styleRules,
    ActiveStyles activeStyles,
    PropertyContainer asPropertyView,
    boolean important
  ) {
    for (WeightedStyleRule styleRule: styleRules) {
      for (Declaration declaration: styleRule.rule().declarations()) {
        if (declaration.name().startsWith("--")) continue;
        if (declaration.important() != important) continue;
        parseDeclaration(declaration, activeStyles, asPropertyView);
      }
    }
  }

  private static void addCustomDeclarations(
    Collection<WeightedStyleRule> styleRules,
    ActiveStyles activeStyles,
    boolean important
  ) {
    for (WeightedStyleRule styleRule: styleRules) {
      for (Declaration declaration: styleRule.rule().declarations()) {
        if (!declaration.name().startsWith("--")) continue;
        if (declaration.important() != important) continue;
        parseCustomDeclaration(activeStyles, declaration);
      }
    }
  }

  private static void parseDeclaration(
    Declaration declaration,
    ActiveStyles activeStyles,
    PropertyContainer asPropertyView
  ) {
    PropertyValueParser declarationDetails = DeclarationParser.declarationDetails(declaration.name());
    if (declarationDetails == null) return;
    CSSValue declValue = declaration.evaluate();

    if (declValue instanceof CSSDeferred deferredValue) {
      declValue = CommonUtil.rethrow(() -> DeclarationParser.parseDeferredDeclaration(
        declaration.source(), deferredValue, asPropertyView));
    }

    switch (declValue) {
      case CSSValue.SpecialCSSValue.INITIAL -> activeStyles.useInitialProperty(declarationDetails.relatedProperty());
      case CSSValue.SpecialCSSValue.INHERIT -> activeStyles.inheritProperty(declarationDetails.relatedProperty());
      case CSSValue.SpecialCSSValue.UNSET -> activeStyles.unsetProperty(declarationDetails.relatedProperty());
      case CSSValue.SpecialCSSValue.INVALID -> {}
      default -> {
        // So that shorthand properties reset unspecified properties
        if (declarationDetails.relatedProperty().hasExpansion()) {
          activeStyles.useInitialProperty(declarationDetails.relatedProperty());
        }
        declarationDetails.updateProperty(declValue, activeStyles);
      }
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
