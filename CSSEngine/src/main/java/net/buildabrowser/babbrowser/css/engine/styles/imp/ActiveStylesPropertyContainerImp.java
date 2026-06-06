package net.buildabrowser.babbrowser.css.engine.styles.imp;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;

public class ActiveStylesPropertyContainerImp implements PropertyContainer {

  private final PropertyContainer parent;
  private final ActiveStyles activeStyles;

  public ActiveStylesPropertyContainerImp(
    PropertyContainer parent,
    ActiveStyles activeStyles
  ) {
    this.parent = parent;
    this.activeStyles = activeStyles;
  }

  @Override
  public PropertyContainer parent() {
    return this.parent;
  }

  @Override
  public boolean wasInherited(CSSProperty property) {
    return parent != null && activeStyles.shouldInherit(property);
  }

  @Override
  public CSSValue get(CSSProperty property) {
    return activeStyles.getProperty(parent, property);
  }

  @Override
  public CSSValue getCustom(String property) {
    return activeStyles.getCustom(property);
  }
  
}
