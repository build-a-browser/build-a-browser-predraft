package net.buildabrowser.babbrowser.css.engine.styles;

import net.buildabrowser.babbrowser.css.engine.styles.imp.ActiveStylesImp;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;

public interface ActiveStyles extends PropertyContainer {

  ActiveStyles parent();

  void inheritProperty(CSSProperty property);

  void useInitialProperty(CSSProperty property);

  void useInitialCustomProperty(String property);

  void unsetProperty(CSSProperty property);

  boolean wasInherited(CSSProperty property);

  static ActiveStyles create() {
    return new ActiveStylesImp(null);
  }

  static ActiveStyles create(ActiveStyles parentStyles) {
    return new ActiveStylesImp(parentStyles);
  }

}
