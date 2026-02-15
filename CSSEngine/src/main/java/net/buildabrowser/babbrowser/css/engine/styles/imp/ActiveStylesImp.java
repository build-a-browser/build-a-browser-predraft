package net.buildabrowser.babbrowser.css.engine.styles.imp;

import java.util.BitSet;

import net.buildabrowser.babbrowser.common.datastruct.SinglyLinkedList;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.CSSValue;
import net.buildabrowser.babbrowser.css.engine.property.color.ColorValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.css.engine.property.display.DisplayValue.OuterDisplayValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public class ActiveStylesImp implements ActiveStyles {

  private final ActiveStyles parentStyles;
  private final BitSet inheritValues;
  private final BitSet hasOwnValues;

  private SinglyLinkedList<CSSValue> activeProperties;

  public ActiveStylesImp(ActiveStyles parentStyles) {
    this.parentStyles = parentStyles;
    this.inheritValues = new BitSet(CSSProperty.idCount());
    this.hasOwnValues = new BitSet(CSSProperty.idCount());
  }

  @Override
  public void setProperty(CSSProperty property, CSSValue value) {
    if (property.hasExpansion()) {
      throw new UnsupportedOperationException("Cannot set expanded property!");
    }

    addEntry(property.id(), value);
    inheritValues.set(property.id(), false);
  }

  @Override
  public void inheritProperty(CSSProperty property) {
    if (property.hasExpansion()) {
      for (CSSProperty expansion: property.getExpansions()) {
        inheritProperty(expansion);
      }
    } else {
      removeEntry(property.id());
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
    removeEntry(property.id());
    inheritValues.set(property.id(), false);
  }

  @Override
  public CSSValue getProperty(CSSProperty property) {
    if (property.hasExpansion()) {
      throw new UnsupportedOperationException("Cannot get expanded property!");
    }

    int id = property.id();
    if (hasOwnValues.get(id)) {
      return scanValue(id);
    }

    return parentStyles != null && (property.inherited() || inheritValues.get(id)) ?
      parentStyles.getProperty(property) :
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

  private CSSValue scanValue(int id) {
    if (!hasOwnValues.get(id)) return null;
    int listPos = getPropertyPos(id);
    return SinglyLinkedList.get(activeProperties, listPos);
  }
  
  private void addEntry(int id, CSSValue value) {
    boolean wasPresent = hasOwnValues.get(id);
    int listPos = getPropertyPos(id);

    if (wasPresent) {
      SinglyLinkedList.replace(activeProperties, listPos, value);
    } else {
      activeProperties = SinglyLinkedList.insert(activeProperties, listPos, value);
    }
    hasOwnValues.set(id, true);
  }

  private void removeEntry(int id) {
    if (!hasOwnValues.get(id)) return;
    hasOwnValues.set(id, false);

    int listPos = getPropertyPos(id);
    activeProperties = SinglyLinkedList.remove(activeProperties, listPos);
  }

  private int getPropertyPos(int id) {
    int listPos = 0;
    int currentId = 0;
    while (currentId < id) {
      if (hasOwnValues.get(currentId)) {
        listPos++;
      }
      currentId++;
    }
    return listPos;
  }
  
}
