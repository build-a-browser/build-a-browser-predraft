package net.buildabrowser.babbrowser.css.engine.styles;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.floats.ClearValue.ClearSide;
import net.buildabrowser.babbrowser.css.engine.property.floats.FloatValue.FloatSide;
import net.buildabrowser.babbrowser.css.engine.styles.imp.ActiveStylesImp;

public interface ActiveStyles {
  
  int textColor();

  void setTextColor(int textColor);

  OuterDisplayValue outerDisplayValue();

  InnerDisplayValue innerDisplayValue();

  void setOuterDisplayValue(OuterDisplayValue outerDisplayValue);

  void setInnerDisplayValue(InnerDisplayValue innerDisplayValue);

  ClearSide clearSide();

  FloatSide floatSide();

  void setClear(CSSValue result);

  void setFloat(CSSValue result);

  static ActiveStyles create() {
    return new ActiveStylesImp();
  }

}
