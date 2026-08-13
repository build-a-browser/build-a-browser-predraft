package net.buildabrowser.babbrowser.renderer.context.imp;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.selector.SelectorTarget;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;

public abstract class RenderContextImp implements RenderContext, PropertyContainer {

  private final short slotFamilyId;
  // TODO: Remove the need for this field

  private short invalidationLevel = InvalidationLevel.NONE;
  protected ActiveStyles activeStyles;
  // ELEMENT is not stored in targetedProperties because it is common, so we avoid the wrapper tax
  protected TargetedPropertiesHolder targetedProperties;
  private RenderContext next;

  public RenderContextImp(short slotFamily) {
    this.slotFamilyId = slotFamily;
  }

  @Override
  public PropertyContainer properties() {
    assert this.activeStyles != null;
    return this;
  }
  
  @Override
  public PropertyContainer targetedProperties(SelectorTarget target) {
    assert this.activeStyles != null;
    if (target.equals(SelectorTarget.ELEMENT)) {
      return this;
    }

    TargetedPropertiesHolder holder = IntrusiveList.find(
      targetedProperties, h -> h.target().equals(target));
    if (holder == null) return null;
    assert holder.container() != null;
    return holder.container();
  }

  protected void invalidateIfChangedStyles(ActiveStyles oldStyles, PropertyContainer parentProperties) {
    if (oldStyles == null) {
      invalidate(InvalidationLevel.ALL);
    } else {
      // TODO: This is an inefficient way to do this, but we can't put a change listener on the
      //   ActiveStyles since it is regenerated from scratch (to make sure selector specificity,
      //   vars, etc. are respected each render)
      for (CSSProperty property : CSSProperty.values()) {
        if (property.hasExpansion()) continue;
        CSSValue newValue = activeStyles.getProperty(parentProperties, property);
        CSSValue oldValue = oldStyles.getProperty(parentProperties, property);
        if (!newValue.equals(oldValue)) {
          invalidate(property.invalidationLevel());
        }
      }
    }
  }

  // Directly implement PropertyContainer to save some allocations

  @Override
  public PropertyContainer parent() {
    return null;
  }

  @Override
  public boolean wasInherited(CSSProperty property) {
    return parent() != null && activeStyles.shouldInherit(property);
  }

  @Override
  public CSSValue get(CSSProperty property) {
    return activeStyles.getProperty(parent(), property);
  }

  @Override
  public CSSValue getCustom(String property) {
    return activeStyles.getCustom(property);
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
    this.invalidationLevel = (short) (this.invalidationLevel | invalidationLevel);
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
