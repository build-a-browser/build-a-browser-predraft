package net.buildabrowser.babbrowser.css.engine.styles;

import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.css.engine.styles.imp.ActiveStylesImp;

public interface ActiveStyles {
  
  int textColor();

  void setTextColor(int textColor);

  OuterDisplayValue outerDisplayValue();

  InnerDisplayValue innerDisplayValue();

  void setOuterDisplayValue(OuterDisplayValue outerDisplayValue);

  void setInnerDisplayValue(InnerDisplayValue innerDisplayValue);

  static ActiveStyles create() {
    return new ActiveStylesImp();
  }

}
