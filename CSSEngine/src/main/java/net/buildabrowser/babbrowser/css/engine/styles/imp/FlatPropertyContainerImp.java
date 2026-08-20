package net.buildabrowser.babbrowser.css.engine.styles.imp;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.CachedFlattenPropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;

public class FlatPropertyContainerImp extends SparsePropertyHolderWithInherit
  implements CachedFlattenPropertyContainer {

  private final PropertyContainer parent;
  // TODO: With a HashMap, ActiveStyles won't be GC'd, but with a WeakHashMap, they might be GC'd before
  // the layout pass ends...
  private final Map<ActiveStyles, WeakReference<PropertyContainer>> childCache = new HashMap<>();

  private final Map<String, CSSValue> customProperties;

  public FlatPropertyContainerImp(
    PropertyContainer parent,
    Map<String, CSSValue> customProperties
  ) {
    this.parent = parent;
    this.customProperties = customProperties;
  }

  // Only used during var resolution in initial construction
  // It should still be safe to cache and re-use
  @Override
  public PropertyContainer parent() {
    return this.parent;
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

  public void addProperty(CSSProperty property, CSSValue value, boolean inherited) {
    if (property.hasExpansion()) {
      throw new UnsupportedOperationException("Cannot set expanded property!");
    }
    
    setInheritValue(property.id(), inherited);
    if (value.equals(property.initial()) && !property.inherited()) return;
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
    WeakReference<PropertyContainer> ref = childCache.get(activeStyles);
    return ref == null ? null : ref.get();
  }

  @Override
  public void put(ActiveStyles activeStyles, PropertyContainer props) {
    childCache.put(activeStyles, new WeakReference<>(props));
  }
  
}
