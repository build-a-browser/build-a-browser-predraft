package net.buildabrowser.babbrowser.css.engine.styles;

import net.buildabrowser.babbrowser.css.engine.styles.imp.ActiveStylesImp;
import net.buildabrowser.babbrowser.css.engine.styles.imp.ActiveStylesPropertyContainerImp;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.MutablePropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;

public interface ActiveStyles extends MutablePropertyContainer {

  void inheritProperty(CSSProperty property);

  void useInitialProperty(CSSProperty property);

  void useInitialCustomProperty(String property);

  void unsetProperty(CSSProperty property);

  boolean shouldInherit(CSSProperty property);

  static ActiveStyles create() {
    return new ActiveStylesImp();
  }

  static PropertyContainer parentStyles(
    PropertyContainer parentContainer,
    ActiveStyles activeStyles
  ) {
    return new ActiveStylesPropertyContainerImp(parentContainer, activeStyles);
  }

  static PropertyContainer unparentedStyles(
    ActiveStyles activeStyles
  ) {
    return new ActiveStylesPropertyContainerImp(null, activeStyles);
  }

}
