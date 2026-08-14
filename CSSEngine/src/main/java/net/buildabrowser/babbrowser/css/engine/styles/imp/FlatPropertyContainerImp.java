package net.buildabrowser.babbrowser.css.engine.styles.imp;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.CachedFlattenPropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;

public class FlatPropertyContainerImp extends SparsePropertyHolder
  implements CachedFlattenPropertyContainer {

  private final Map<ActiveStyles, PropertyContainer> childCache = new HashMap<>();

  private final Map<String, CSSValue> customProperties;
  private final boolean isReusable;

  public FlatPropertyContainerImp(
    Map<String, CSSValue> customProperties,
    boolean isReusable
  ) {
    this.customProperties = customProperties;
    this.isReusable = isReusable;
  }

  @Override
  public PropertyContainer parent() {
    throw new UnsupportedOperationException("Unimplemented method 'parent'");
  }

  @Override
  public boolean wasInherited(CSSProperty property) {
    return
      getInheritValue(property.id())
      || (!getHasOwnValue(property.id()) && property.inherited());
  }

  @Override
  public CSSValue get(CSSProperty property) {
    if (property.hasExpansion()) {
      throw new UnsupportedOperationException("Cannot get expanded property!");
    }

    int id = property.id();
    return getHasOwnValue(id) ?
      scanValue(id) :
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
  public boolean isReusable() {
    return this.isReusable;
  }

  public void addProperty(CSSProperty property, CSSValue value, boolean inherited) {
    if (property.hasExpansion()) {
      throw new UnsupportedOperationException("Cannot set expanded property!");
    }
    
    setInheritValue(property.id(), inherited);
    if (value.equals(property.initial())) return;
    addEntry(property.id(), value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FlatPropertyContainerImp other)) return false;

    return
      abstractEquals(o)
      && Objects.equals(customProperties, other.customProperties);
  }

  @Override
  public int hashCode() {
    int hashCode = abstractHashCode();
    hashCode = 31 * hashCode + Objects.hash(customProperties);
    return hashCode;
  }

  @Override
  public PropertyContainer get(ActiveStyles activeStyles) {
    return childCache.get(activeStyles);
  }

  @Override
  public void put(ActiveStyles activeStyles, PropertyContainer props) {
    childCache.put(activeStyles, props);
  }
  
}
