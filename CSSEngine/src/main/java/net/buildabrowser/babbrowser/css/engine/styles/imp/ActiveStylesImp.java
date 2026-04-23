package net.buildabrowser.babbrowser.css.engine.styles.imp;

import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.common.datastruct.SinglyLinkedList;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;

public class ActiveStylesImp implements ActiveStyles {

  static {
    if (CSSProperty.idCount() > 63) {
      throw new RuntimeException(
        "Property count greater than available bits: Please optimize");
    }
  }

  private final ActiveStyles parentStyles;

  // A BitSet has a header and long array. Even with a lazy initialization attempt
  // that took a lot of memory. Use longs instead
  private long inheritValues;
  private long hasOwnValues;

  // TODO: Switch to an IntrusiveList?
  private SinglyLinkedList<CSSValue> activeProperties;
  private Map<String, CSSValue> customProperties;

  public ActiveStylesImp(ActiveStyles parentStyles) {
    this.parentStyles = parentStyles;
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
    setInheritValue(property.id(), false);
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
      setInheritValue(property.id(), true);
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
    setInheritValue(property.id(), false);
  }

  @Override
  public CSSValue getProperty(CSSProperty property) {
    if (property.hasExpansion()) {
      throw new UnsupportedOperationException("Cannot get expanded property!");
    }

    int id = property.id();
    if (getHasOwnValue(id)) {
      return scanValue(id);
    }

    return parentStyles != null && (property.inherited() || getInheritValue(id)) ?
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
      && (property.inherited() || getInheritValue(property.id()))
      && !getHasOwnValue(property.id());
  }

  private CSSValue scanValue(int id) {
    if (!getHasOwnValue(id)) return null;
    int listPos = getPropertyPos(id);
    return IntrusiveList.get(activeProperties, listPos).item();
  }
  
  private void addEntry(int id, CSSValue value) {
    assert value != null;
    boolean wasPresent = getHasOwnValue(id);
    int listPos = getPropertyPos(id);

    if (wasPresent) {
      activeProperties = IntrusiveList.replace(activeProperties, listPos, new SinglyLinkedList<>(value));
    } else {
      activeProperties = IntrusiveList.insert(activeProperties, listPos, new SinglyLinkedList<>(value));
    }
    setHasOwnValue(id, true);
  }

  private void removeEntry(int id) {
    if (!getHasOwnValue(id)) return;
    setHasOwnValue(id, false);

    int listPos = getPropertyPos(id);
    activeProperties = IntrusiveList.remove(activeProperties, listPos);
  }

  private int getPropertyPos(int id) {
    int listPos = 0;
    int currentId = hasOwnValuesNextSetBit(0);
    while (currentId < id && currentId != -1) {
      listPos++;
      currentId = hasOwnValuesNextSetBit(currentId + 1);
    }
    return listPos;
  }

  private void lazilyInitCustomProperties() {
    if (customProperties == null) {
      this.customProperties = new HashMap<>(4);
    }
  }

  private void setInheritValue(int id, boolean b) {
    if (b) {
      inheritValues |= (1L << id);
    } else {
      inheritValues &= ~(1L << id);
    }
  }

  private boolean getInheritValue(int id) {
    return (inheritValues & (1L << id)) != 0;
  }

  private void setHasOwnValue(int id, boolean b) {
    if (b) {
      hasOwnValues |= (1L << id);
    } else {
      hasOwnValues &= ~(1L << id);
    }
  }

  private boolean getHasOwnValue(int id) {
    return (hasOwnValues & (1L << id)) != 0;
  }

  private int hasOwnValuesNextSetBit(int fromIndex) {
    long word = hasOwnValues & (-1L << fromIndex);
    if (word == 0) return -1;
    return Long.numberOfTrailingZeros(word);
  }
  
}
