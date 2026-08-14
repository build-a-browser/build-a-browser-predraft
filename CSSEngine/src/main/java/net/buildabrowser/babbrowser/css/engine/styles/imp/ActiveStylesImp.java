package net.buildabrowser.babbrowser.css.engine.styles.imp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.CachedFlattenPropertyContainer;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSDeferred;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;

public class ActiveStylesImp extends SparsePropertyHolder implements ActiveStyles {

  static {
    if (CSSProperty.idCount() > 127) {
      throw new RuntimeException(
        "Property count greater than available bits: Please optimize");
    }
  }

  private final Collection<WeightedStyleRule> refRules;
  
  private Map<String, CSSValue> customProperties;
  private boolean isReusable = true;

  public ActiveStylesImp(Collection<WeightedStyleRule> refRules) {
    this.refRules = refRules;
  }

  @Override
  public Collection<WeightedStyleRule> refRules() {
    return refRules;
  }

  @Override
  public void setProperty(CSSProperty property, CSSValue value) {
    if (property.hasExpansion()) {
      throw new UnsupportedOperationException("Cannot set expanded property!");
    } else if (value instanceof CSSDeferred) {
      this.isReusable = false;
    }

    addEntry(property.id(), value);
    setInheritValue(property.id(), false);
  }

  @Override
  public void setCustomProperty(String property, CSSValue value) {
    if (value instanceof CSSDeferred) {
      this.isReusable = false;
    }

    lazilyInitCustomProperties();
    customProperties.put(property, value);
  }

  @Override
  public void inheritProperty(CSSProperty property) {
    if (property.hasExpansion()) {
      for (CSSProperty expansion: property.getExpansions()) {
        inheritProperty(expansion);
      }
    } else {
      removeEntry(property.id());
      setInheritValue(property.id(), true);
    }
  }

  @Override
  public void useInitialProperty(CSSProperty property) {
    if (property.hasExpansion()) {
      for (CSSProperty expansion: property.getExpansions()) {
        useInitialProperty(expansion);
      }
    } else {
      setProperty(property, property.initial());
    }
  }

  @Override
  public void useInitialCustomProperty(String property) {
    lazilyInitCustomProperties();
    customProperties.put(property, null);
  }

  @Override
  public void unsetProperty(CSSProperty property) {
    if (property.hasExpansion()) {
      for (CSSProperty expansion: property.getExpansions()) {
        unsetProperty(expansion);
      }
    } else {
      removeEntry(property.id());
      setInheritValue(property.id(), false);
    }
  }

  @Override
  public CSSValue getProperty(
    PropertyContainer parent,
    CSSProperty property
  ) {
    if (property.hasExpansion()) {
      throw new UnsupportedOperationException("Cannot get expanded property!");
    }

    int id = property.id();
    if (getHasOwnValue(id)) {
      return scanValue(id);
    }

    return parent != null && (property.inherited() || getInheritValue(id)) ?
      parent.get(property) :
      property.initial();
  }

  @Override
  public CSSValue getCustom(String property) {
    if (
      customProperties == null
      || !customProperties.containsKey(property)
    ) {
      return CSSFailure.UNSET_CUSTOM_PROPERTY;
    }

    return customProperties.get(property);
  }

  @Override
  public boolean shouldInherit(CSSProperty property) {
    return
     (property.inherited() || getInheritValue(property.id()))
      && !getHasOwnValue(property.id());
  }

  @Override
  public boolean isReusable() {
    return this.isReusable;
  }

  @Override
  public void disallowReuse() {
    this.isReusable = false;
  }

  @Override
  public Map<String, CSSValue> customProperties() {
    return this.customProperties;
  }

  @Override
  public PropertyContainer flatten(
    PropertyContainer parent,
    Function<PropertyContainer, PropertyContainer> cacheFunc
  ) {
    if (parent instanceof CachedFlattenPropertyContainer ccProperties) {
      PropertyContainer cached = ccProperties.get(this);
      if (cached != null) return cached;
    }

    FlatPropertyContainerImp flattened = new FlatPropertyContainerImp(
      customProperties(), isReusable());
    forEachSet((property, value) -> {
      flattened.addProperty(property, value, false);
    });
    forEachInherited((property, isManual) -> {
      CSSValue value = getProperty(parent, property);
      flattened.addProperty(property, value, true);
    });
    flattened.freeze();

    PropertyContainer props = cacheFunc.apply(flattened);
    if (parent instanceof CachedFlattenPropertyContainer ccProperties) {
      ccProperties.put(this, props);
    }

    return props;
  }

  private void lazilyInitCustomProperties() {
    if (customProperties == null) {
      this.customProperties = new HashMap<>(4);
    }
  }
  
}
