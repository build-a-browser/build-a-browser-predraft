package net.buildabrowser.babbrowser.css.engine.styles;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.css.engine.styles.imp.ActiveStylesImp;

public interface ActiveStyles {

  void setProperty(CSSProperty property, CSSValue value);

  void inheritProperty(CSSProperty property);

  void useInitialProperty(CSSProperty property);

  void unsetProperty(CSSProperty property);

  CSSValue getProperty(CSSProperty property);

  int textColor();

  int backgroundColor();

  int borderTopColor();
  
  int borderBottomColor();

  int borderLeftColor();

  int borderRightColor();

  OuterDisplayValue outerDisplayValue();

  InnerDisplayValue innerDisplayValue();

  static ActiveStyles create() {
    return new ActiveStylesImp(null);
  }

  static ActiveStyles create(ActiveStyles parentStyles) {
    return new ActiveStylesImp(parentStyles);
  }

}
