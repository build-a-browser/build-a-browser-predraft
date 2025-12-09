package net.buildabrowser.babbrowser.css.engine.styles.imp;

import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.floats.ClearValue;
import net.buildabrowser.babbrowser.css.engine.property.floats.FloatValue;
import net.buildabrowser.babbrowser.css.engine.property.size.LengthValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public class ActiveStylesImp implements ActiveStyles {

  private static final CSSValue zeroValue = LengthValue.create(0, true, null);

  private final CSSValue[] cssValues = new CSSValue[] {
    zeroValue, zeroValue, zeroValue, zeroValue,
    zeroValue, zeroValue, zeroValue, zeroValue,
    CSSValue.AUTO, CSSValue.AUTO, CSSValue.AUTO, CSSValue.AUTO,
    CSSValue.AUTO, zeroValue, CSSValue.NONE,
    CSSValue.AUTO, zeroValue, CSSValue.NONE
  };

  private int textColor = 0xFF000000;
  private int backgroundColor = 0xFFFFFFFF;
  private OuterDisplayValue outerDisplayValue = OuterDisplayValue.BLOCK;
  private InnerDisplayValue innerDisplayValue = InnerDisplayValue.FLOW;
  private CSSValue floatSide = CSSValue.NONE;
  private CSSValue clearSide = CSSValue.NONE;

  @Override
  public int textColor() {
    return this.textColor;
  }

  @Override
  public void setTextColor(int textColor) {
    this.textColor = textColor;
  }

  @Override
  public int backgroundColor() {
    return this.backgroundColor;
  }

  @Override
  public void setBackgroundColor(int backgroundColor) {
    this.backgroundColor = backgroundColor;
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

  @Override
  public CSSValue floatSide() {
    return this.floatSide;
  }

  @Override
  public CSSValue clearSide() {
    return this.clearSide;
  }

  @Override
  public void setFloat(CSSValue result) {
    // TODO: Handle inherit
    if (result instanceof FloatValue floatValue) {
      this.floatSide = floatValue;
    }
  }

  @Override
  public void setClear(CSSValue result) {
    // TODO: Handle inherit
    if (result instanceof ClearValue clearValue) {
      this.clearSide = clearValue;
    }
  }

  @Override
  public void setSizingProperty(SizingUnit unit, CSSValue value) {
    cssValues[unit.ordinal()] = value;
  }

  @Override
  public CSSValue getSizingProperty(SizingUnit unit) {
    return cssValues[unit.ordinal()];
  }
  
}
