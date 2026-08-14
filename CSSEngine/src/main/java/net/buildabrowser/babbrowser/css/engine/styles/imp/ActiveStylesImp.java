package net.buildabrowser.babbrowser.css.engine.styles.imp;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesFlattener;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.SpecialCSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;

public class ActiveStylesImp extends SparsePropertyHolder implements ActiveStyles {

  static {
    if (CSSProperty.idCount() > 127) {
      throw new RuntimeException(
        "Property count greater than available bits: Please optimize");
    }
  }

  private final Collection<WeightedStyleRule> refRules;
  
  // TODO: Switch to IntrusiveList?
  private Map<String, CSSValue> customProperties;

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
    }

    addEntry(property.id(), value);
  }

  @Override
  public void setCustomProperty(String property, CSSValue value) {
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
      if (property.inherited()) {
        unsetProperty(property);
      } else {
        setProperty(property, SpecialCSSValue.INHERIT);
      }
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
    }
  }

  @Override
  public CSSValue getProperty(
    CSSProperty property
  ) {
    if (property.hasExpansion()) {
      throw new UnsupportedOperationException("Cannot get expanded property!");
    }

    int id = property.id();
    if (getHasOwnValue(id)) {
      return scanValue(id);
    }

    return property.inherited() ?
      SpecialCSSValue.INHERIT :
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
  public Map<String, CSSValue> customProperties() {
    return this.customProperties;
  }

  @Override
  public PropertyContainer flatten(
    PropertyContainer parent,
    Function<PropertyContainer, PropertyContainer> cacheFunc
  ) {
    return ActiveStylesFlattener.flatten(this, parent, cacheFunc);
  }

  private void lazilyInitCustomProperties() {
    if (customProperties == null) {
      this.customProperties = new HashMap<>(4);
    }
  }

  @Override
  public int hashCode() {
    return 31 * Objects.hash(refRules, customProperties) + abstractHashCode();
  }

  static int i = 0;
  @Override
  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof ActiveStylesImp other)) return false;
    boolean match =
      super.abstractEquals(o)
      && Objects.equals(refRules, other.refRules)
      && Objects.equals(customProperties, other.customProperties);
    return match;
  }
  
}
