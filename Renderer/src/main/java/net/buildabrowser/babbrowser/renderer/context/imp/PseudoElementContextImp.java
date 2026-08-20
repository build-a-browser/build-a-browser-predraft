package net.buildabrowser.babbrowser.renderer.context.imp;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.context.RenderContext;
import net.buildabrowser.babbrowser.renderer.context.TargetedPropertiesHolder;
import net.buildabrowser.babbrowser.renderer.style.StyleCache;

// TODO: It might be worthwhile to switch to a standard ElementContextImp in the future
// Then that class would not need as much special-case styling code for this
public class PseudoElementContextImp implements RenderContext {
  
  // TODO: Remove the need for this field
  private final short slotFamilyId;
  private final TargetedPropertiesHolder targetHolder;

  private ElementBox box;
  private short invalidationLevel = InvalidationLevel.BOX;
  private RenderContext next;

  public PseudoElementContextImp(
    short slotFamily,
    TargetedPropertiesHolder targetHolder
  ) {
    this.slotFamilyId = slotFamily;
    this.targetHolder = targetHolder;
  }

  @Override
  public PropertyContainer properties() {
    return targetHolder.container();
  }

  @Override
  public ActiveStyles regenerateStyles(StyleCache styleCache, ActiveStyles refStyles) {
    throw new UnsupportedOperationException("Unimplemented method 'regenerateStyles'");
  }

  @Override
  public HTMLElement element() {
    // TODO: Is it better to return the parent element?
    return null;
  }

  @Override
  public ElementBox box() {
    return this.box;
  }

  @Override
  public void setBox(ElementBox box) {
    this.box = box;
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
