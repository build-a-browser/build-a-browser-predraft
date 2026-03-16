package net.buildabrowser.babbrowser.css.engine.styles.imp;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.common.datastruct.SinglyLinkedList;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.OuterDisplayValue;

public class ActiveStylesImp implements ActiveStyles {

  private final ActiveStyles parentStyles;
  private final BitSet inheritValues;
  private final BitSet hasOwnValues;

  // TODO: Switch to an IntrusiveList?
  private SinglyLinkedList<CSSValue> activeProperties;
  private Map<String, CSSValue> customProperties;

  public ActiveStylesImp(ActiveStyles parentStyles) {
    this.parentStyles = parentStyles;
    this.inheritValues = new BitSet(CSSProperty.idCount());
    this.hasOwnValues = new BitSet(CSSProperty.idCount());
  }

  @Override
  public ActiveStyles parent() {
    return this.parentStyles;
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
  public void setCustomProperty(String property, CSSValue value) {
    lazilyInitCustomProperties();

    customProperties.put(property, value);
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
  public void useInitialCustomProperty(String property) {
    lazilyInitCustomProperties();
    customProperties.put(property, null);
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
  public CSSValue getCustomProperty(String property) {
    if (
      customProperties == null
      || !customProperties.containsKey(property)
    ) {
      return CSSFailure.UNSET_CUSTOM_PROPERTY;
    }

    return customProperties.get(property);
  }

  @Override
  public boolean wasInherited(CSSProperty property) {
    return
      parentStyles != null
      && (property.inherited() || inheritValues.get(property.id()))
      && !hasOwnValues.get(property.id());
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
    return IntrusiveList.get(activeProperties, listPos).item();
  }
  
  private void addEntry(int id, CSSValue value) {
    boolean wasPresent = hasOwnValues.get(id);
    int listPos = getPropertyPos(id);

    if (wasPresent) {
      activeProperties = IntrusiveList.replace(activeProperties, listPos, new SinglyLinkedList<>(value));
    } else {
      activeProperties = IntrusiveList.insert(activeProperties, listPos, new SinglyLinkedList<>(value));
    }
    hasOwnValues.set(id, true);
  }

  private void removeEntry(int id) {
    if (!hasOwnValues.get(id)) return;
    hasOwnValues.set(id, false);

    int listPos = getPropertyPos(id);
    activeProperties = IntrusiveList.remove(activeProperties, listPos);
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

  private void lazilyInitCustomProperties() {
    if (customProperties == null) {
      this.customProperties = new HashMap<>(4);
    }
  }
  
}
