package net.buildabrowser.babbrowser.css.engine.styles.imp;

import java.util.BitSet;

import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

// TODO: Storage really needs optimized...
public class ActiveStylesImp implements ActiveStyles {

  private final CSSValue[] propertyValues;
  private final BitSet inheritValues;

  private final ActiveStyles parentStyles;

  public ActiveStylesImp(ActiveStyles parentStyles) {
    this.parentStyles = parentStyles;
    this.propertyValues = new CSSValue[CSSProperty.idCount()];
    this.inheritValues = new BitSet(CSSProperty.idCount());
  }

  @Override
  public void setProperty(CSSProperty property, CSSValue value) {
    if (property.hasExpansion()) {
      throw new UnsupportedOperationException("Cannot set expanded property!");
    }

    propertyValues[property.id()] = value;
    inheritValues.set(property.id(), false);
  }

  @Override
  public void inheritProperty(CSSProperty property) {
    if (property.hasExpansion()) {
      for (CSSProperty expansion: property.getExpansions()) {
        inheritProperty(expansion);
      }
    } else {
      propertyValues[property.id()] = null;
      inheritValues.set(property.id(), true);
    }
  }

  @Override
  public void useInitialProperty(CSSProperty property) {
    if (property.hasExpansion()) {
      for (CSSProperty expansion: property.getExpansions()) {
        useInitialProperty(expansion);
      }
    } else {
      setProperty(property, property.initial());
    }
  }

  @Override
  public void unsetProperty(CSSProperty property) {
    propertyValues[property.id()] = null;
    inheritValues.set(property.id(), false);
  }

  @Override
  public CSSValue getProperty(CSSProperty property) {
    if (property.hasExpansion()) {
      throw new UnsupportedOperationException("Cannot get expanded property!");
    }

    int id = property.id();
    return
      parentStyles != null && inheritValues.get(id) ? parentStyles.getProperty(property) :
      propertyValues[id] != null ? propertyValues[id] :
      parentStyles != null && property.inherited() ? parentStyles.getProperty(property) :
      property.initial();
  }

  @Override
  public int textColor() {
    return ((ColorValue) getProperty(CSSProperty.COLOR)).asSARGB();
  }

  @Override
  public int backgroundColor() {
    return ((ColorValue) getProperty(CSSProperty.BACKGROUND_COLOR)).asSARGB();
  }

  @Override
  public int borderTopColor() {
    CSSValue property = getProperty(CSSProperty.BORDER_TOP_COLOR);
    if (property.equals(CSSValue.NONE)) return textColor();
    return ((ColorValue) property).asSARGB();
  }

  @Override
  public int borderBottomColor() {
    CSSValue property = getProperty(CSSProperty.BORDER_BOTTOM_COLOR);
    if (property.equals(CSSValue.NONE)) return textColor();
    return ((ColorValue) property).asSARGB();
  }

  @Override
  public int borderLeftColor() {
    CSSValue property = getProperty(CSSProperty.BORDER_LEFT_COLOR);
    if (property.equals(CSSValue.NONE)) return textColor();
    return ((ColorValue) property).asSARGB();
  }

  @Override
  public int borderRightColor() {
    CSSValue property = getProperty(CSSProperty.BORDER_RIGHT_COLOR);
    if (property.equals(CSSValue.NONE)) return textColor();
    return ((ColorValue) property).asSARGB();
  }

  @Override
  public OuterDisplayValue outerDisplayValue() {
    return ((DisplayValue) getProperty(CSSProperty.DISPLAY)).outerDisplayValue();
  }

  @Override
  public InnerDisplayValue innerDisplayValue() {
    return ((DisplayValue) getProperty(CSSProperty.DISPLAY)).innerDisplayValue();
  }
  
}
