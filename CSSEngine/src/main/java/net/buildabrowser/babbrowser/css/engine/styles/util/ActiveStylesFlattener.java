package net.buildabrowser.babbrowser.css.engine.styles.util;

import java.util.function.Function;

import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.CachedFlattenPropertyContainer;
import net.buildabrowser.babbrowser.css.engine.styles.imp.FlatPropertyContainerImp;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSDeferred;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSDeferredWithFallback;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.SpecialCSSValue;
import net.buildabrowser.babbrowser.cssbase.property.DeclarationParser;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;

public final class ActiveStylesFlattener {
  
  private ActiveStylesFlattener() {}

  public static PropertyContainer flatten(
    ActiveStyles activeStyles,
    PropertyContainer parent,
    Function<PropertyContainer, PropertyContainer> cacheFunc
  ) {
    if (parent instanceof CachedFlattenPropertyContainer ccProperties) {
      PropertyContainer cached = ccProperties.get(activeStyles);
      if (cached != null) return cached;
    }

    FlatPropertyContainerImp flattened = new FlatPropertyContainerImp(
      parent, activeStyles.customProperties());
    if (parent != null) for (CSSProperty property: CSSProperty.values()) {
      // TODO: Pre-compile a list of inherited properties
      if (property.hasExpansion() || !property.inherited()) continue;
      flattened.addProperty(property, parent.get(property), true);
    }
    activeStyles.forEachSet((property, value) -> {
      resolveThenSet(parent, flattened, property, value);
    });
    flattened.freeze();

    PropertyContainer props = cacheFunc.apply(flattened);
    if (parent instanceof CachedFlattenPropertyContainer ccProperties) {
      ccProperties.put(activeStyles, props);
    }

    return props;
  }

  private static void resolveThenSet(
    PropertyContainer parent,
    FlatPropertyContainerImp flattened,
    CSSProperty property,
    CSSValue value
  ) {
    switch (value) {
      case CSSValue.SpecialCSSValue.INITIAL -> {
        if (property.inherited()) {
          flattened.addProperty(property, property.initial(), false);
        }
      }
      case CSSValue.SpecialCSSValue.INHERIT -> {
        if (property.inherited() || parent == null) return;
        flattened.addProperty(property, parent.get(property), true);
      }
      case CSSValue.SpecialCSSValue.UNSET -> {}
      case CSSValue.SpecialCSSValue.INVALID -> {}
      case CSSDeferred deferred -> {
        resolveThenSetDeferred(
          parent,
          flattened,
          property,
          deferred,
          SpecialCSSValue.UNSET);
      }
      case CSSDeferredWithFallback deferred -> {
        resolveThenSetDeferred(
          parent,
          flattened,
          property,
          deferred.inner(),
          deferred.fallback());
      }
      default -> flattened.addProperty(property, value, false);
    }
  }

  private static void resolveThenSetDeferred(
    PropertyContainer parent,
    FlatPropertyContainerImp flattened,
    CSSProperty property,
    CSSDeferred deferred,
    CSSValue fallback
  ) {
    // TODO: Pass valid source
    CSSValue resolved = CommonUtil.rethrow(() ->
      DeclarationParser.parseDeferredDeclaration(deferred, flattened));
    CSSProperty relatedProperty = deferred.parser().relatedProperty();
    if (
      resolved.isFailure()
      || resolved instanceof CSSDeferred
      || resolved instanceof CSSDeferredWithFallback
    ) {
      resolveThenSet(parent, flattened, property, fallback);
    } else if (resolved instanceof SpecialCSSValue) {
      resolveThenSet(parent, flattened, property, resolved);
    } else if (relatedProperty.hasExpansion()) {
      flattened.addProperty(property, property.initial(), false);
      deferred.parser().updateProperty(resolved, (p, v) -> {
        if (p.equals(property)) {
          resolveThenSet(parent, flattened, property, v);
        }
      });
    } else {
      flattened.addProperty(property, resolved, false);
    }
  }

}
