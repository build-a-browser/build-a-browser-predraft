package net.buildabrowser.babbrowser.renderer.context.imp;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;

public abstract class RenderContextImp implements RenderContext {

  private final short slotFamilyId;
  // TODO: Remove the need for this field

  private short invalidationLevel =
    InvalidationLevel.BOX | InvalidationLevel.STYLE_SELF;
  protected PropertyContainer computedStyles;
  private RenderContext next;

  public RenderContextImp(short slotFamily) {
    this.slotFamilyId = slotFamily;
  }

  @Override
  public PropertyContainer properties() {
    assert this.computedStyles != null;
    return this.computedStyles;
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

  // SlotItem

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
