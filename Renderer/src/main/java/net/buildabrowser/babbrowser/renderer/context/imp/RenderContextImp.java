package net.buildabrowser.babbrowser.renderer.context.imp;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.css.engine.styles.CachedFlattenPropertyContainer;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;

public abstract class RenderContextImp implements RenderContext, CachedFlattenPropertyContainer {

  private final short slotFamilyId;
  // TODO: Remove the need for this field

  private short invalidationLevel = InvalidationLevel.BOX;
  protected PropertyContainer computedStyles;
  private RenderContext next;

  public RenderContextImp(short slotFamily) {
    this.slotFamilyId = slotFamily;
  }

  @Override
  public PropertyContainer properties() {
    assert this.computedStyles != null;
    return this;
  }

  protected short changedPropertyInvalidationLevel(
    PropertyContainer oldProperties,
    PropertyContainer newProperties
  ) {
    if (oldProperties == newProperties) {
      return InvalidationLevel.NONE;
    } else if (
      oldProperties == null
      || newProperties == null
    ) {
      return InvalidationLevel.BOX;
    } else {
      short invalidationLevel = InvalidationLevel.NONE;
      for (CSSProperty property : CSSProperty.values()) {
        if (property.hasExpansion()) continue;
        CSSValue newValue = newProperties.get(property);
        CSSValue oldValue = oldProperties.get(property);
        if (!newValue.equals(oldValue)) {
          invalidationLevel |= property.invalidationLevel();
        }
      }

      return invalidationLevel;
    }
  }

  // Previously implemented this way to save some allocations,
  // because this had held ActiveStyles instead of property container.
  // Still needs to be done this way because the new flattened property container
  // does not hold a parent reference, so subclasses override parent.

  @Override
  public PropertyContainer parent() {
    return null;
  }

  @Override
  public boolean wasInherited(CSSProperty property) {
    return parent() != null && computedStyles.wasInherited(property);
  }

  @Override
  public CSSValue get(CSSProperty property) {
    return computedStyles.get(property);
  }

  @Override
  public CSSValue getCustom(String property) {
    return computedStyles.getCustom(property);
  }

  @Override
  public boolean isReusable() {
    return false;
  }

  @Override
  public PropertyContainer get(ActiveStyles activeStyles) {
    if (this.computedStyles instanceof CachedFlattenPropertyContainer propCache) {
      return propCache.get(activeStyles);
    }

    return null;
  }

  @Override
  public void put(ActiveStyles activeStyles, PropertyContainer props) {
    if (this.computedStyles instanceof CachedFlattenPropertyContainer propCache) {
      propCache.put(activeStyles, props);
    }
  }

  // Slottable

  @Override
  public short familyId() {
    return this.slotFamilyId;
  }

  @Override
  public RenderContext next() {
    return this.next;
  }

  @Override
  public void setNext(RenderContext next) {
    this.next = next;
  }

  // Invalidatable

  @Override
  public void invalidate(short invalidationLevel) {
    this.invalidationLevel |= invalidationLevel;
  }

  @Override
  public void validate() {
    this.invalidationLevel = InvalidationLevel.NONE;
  }

  @Override
  public short invalidationLevel() {
    return this.invalidationLevel;
  }
  
}
