package net.buildabrowser.babbrowser.css.engine.styles.imp;

import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public class ActiveStylesImp implements ActiveStyles {

  private int textColor = 0xFF000000;
  private OuterDisplayValue outerDisplayValue = OuterDisplayValue.BLOCK;
  private InnerDisplayValue innerDisplayValue = InnerDisplayValue.FLOW;

  @Override
  public int textColor() {
    return this.textColor;
  }

  @Override
  public void setTextColor(int textColor) {
    this.textColor = textColor;
  }

  @Override
  public OuterDisplayValue outerDisplayValue() {
    return this.outerDisplayValue;
  }

  @Override
  public InnerDisplayValue innerDisplayValue() {
    return this.innerDisplayValue;
  }

  @Override
  public void setOuterDisplayValue(OuterDisplayValue outerDisplayValue) {
    this.outerDisplayValue = outerDisplayValue;
  }

  @Override
  public void setInnerDisplayValue(InnerDisplayValue innerDisplayValue) {
    this.innerDisplayValue = innerDisplayValue;
  }
  
}
