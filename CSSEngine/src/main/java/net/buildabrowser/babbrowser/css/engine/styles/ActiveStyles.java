package net.buildabrowser.babbrowser.css.engine.styles;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.css.engine.styles.imp.ActiveStylesImp;

public interface ActiveStyles {
  
  int textColor();

  void setTextColor(int textColor);

  int backgroundColor();

  void setBackgroundColor(int backgroundColor);

  OuterDisplayValue outerDisplayValue();

  InnerDisplayValue innerDisplayValue();

  void setOuterDisplayValue(OuterDisplayValue outerDisplayValue);

  void setInnerDisplayValue(InnerDisplayValue innerDisplayValue);

  CSSValue clearSide();

  CSSValue floatSide();

  void setClear(CSSValue result);

  void setFloat(CSSValue result);

  void setSizingProperty(SizingUnit unit, CSSValue value);

  CSSValue getSizingProperty(SizingUnit unit);

  static ActiveStyles create() {
    return new ActiveStylesImp();
  }

  static enum SizingUnit {
    MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM, MARGIN_LEFT,
    PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM, PADDING_LEFT,
    // TODO: Border
    TOP, RIGHT, BOTTOM, LEFT,
    WIDTH, MIN_WIDTH, MAX_WIDTH,
    HEIGHT, MIN_HEIGHT, MAX_HEIGHT
  }

}
