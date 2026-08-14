package net.buildabrowser.babbrowser.css.engine.styles.util;

import java.util.ArrayList;
import java.util.Collection;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.Declaration;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.parser.SeekableCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.parser.imp.ListCSSTokenStream;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSDeferred;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSDeferredWithFallback;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSVarValue;
import net.buildabrowser.babbrowser.cssbase.property.CustomPropertyParser;
import net.buildabrowser.babbrowser.cssbase.property.DeclarationParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParser;
import net.buildabrowser.babbrowser.cssbase.tokens.IdentToken;

public final class ActiveStylesGenerator {
  
  private ActiveStylesGenerator() {}

  // TODO: Resolve whatever variables can be immeadietly resolved

  public static ActiveStyles generateActiveStyles(
    Collection<WeightedStyleRule> styleRules // TODO: Switch to just StyleRule
  ) {
    ActiveStyles activeStyles = ActiveStyles.create(styleRules);
    addCustomDeclarations(styleRules, activeStyles, false);
    addCustomDeclarations(styleRules, activeStyles, true);
    // resolveCustomStaticVarReferences(activeStyles);
    addNormalDeclarations(styleRules, activeStyles, false);
    addNormalDeclarations(styleRules, activeStyles, true);
    activeStyles.freeze();

    return activeStyles;
  }

  private static void addNormalDeclarations(
    Collection<WeightedStyleRule> styleRules,
    ActiveStyles activeStyles,
    boolean important
  ) {
    for (WeightedStyleRule styleRule: styleRules) {
      for (Declaration declaration: styleRule.rule().declarations()) {
        if (declaration.name().startsWith("--")) continue;
        if (declaration.important() != important) continue;
        parseDeclaration(declaration, activeStyles);
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
    ActiveStyles activeStyles
  ) {
    PropertyValueParser declarationDetails = DeclarationParser.declarationDetails(declaration.name());
    if (declarationDetails == null) return;
    CSSValue declValue = declaration.evaluate();
    if (declValue instanceof CSSDeferred deferred) {
      CSSValue resolved = staticResolve(deferred, activeStyles, new ArrayList<>());
      if (resolved == null) {
        placeKeyedDeferred(
          declarationDetails.relatedProperty(),
          declarationDetails, deferred, activeStyles);
        return;
      }
      declValue = resolved;
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

  private static CSSValue staticResolve(CSSDeferred deferred, ActiveStyles activeStyles, ArrayList<String> arrayList) {
    return null;
  }

  private static void placeKeyedDeferred(
    CSSProperty target,
    PropertyValueParser parser,
    CSSDeferred deferred,
    ActiveStyles activeStyles
  ) {
    if (target.hasExpansion()) {
      for (CSSProperty property: target.getExpansions()) {
        placeKeyedDeferred(property, parser, deferred, activeStyles);
      }
      return;
    }

    CSSValue fallback = activeStyles.getProperty(target);
    activeStyles.setProperty(target, new CSSDeferredWithFallback(deferred, fallback));
  }

  private static void parseCustomDeclaration(ActiveStyles activeStyles, Declaration declaration) {
    SeekableCSSTokenStream tokenStream = ListCSSTokenStream.createWithSkippedWhitespace(
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
