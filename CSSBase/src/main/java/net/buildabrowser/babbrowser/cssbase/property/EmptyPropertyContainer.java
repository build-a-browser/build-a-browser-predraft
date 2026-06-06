package net.buildabrowser.babbrowser.cssbase.property;

public class EmptyPropertyContainer implements PropertyContainer {

  private final PropertyContainer parentContainer;

  public EmptyPropertyContainer(PropertyContainer parentContainer) {
    this.parentContainer = parentContainer;
  }

  @Override
  public PropertyContainer parent() {
    return this.parentContainer;
  }

  @Override
  public boolean wasInherited(CSSProperty property) {
    return property.inherited();
  }

  @Override
  public CSSValue get(CSSProperty property) {
    return property.inherited() ?
      parentContainer.get(property) :
      property.initial();
  }

  @Override
  public CSSValue getCustom(String property) {
    return CSSValue.NONE;
  }
  
}
