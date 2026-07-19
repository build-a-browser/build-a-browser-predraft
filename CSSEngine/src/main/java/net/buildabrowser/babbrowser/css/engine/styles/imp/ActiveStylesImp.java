package net.buildabrowser.babbrowser.css.engine.styles.imp;

import java.util.HashMap;
import java.util.Map;

import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSDeferred;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue.CSSFailure;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;

public class ActiveStylesImp implements ActiveStyles {

  static {
    if (CSSProperty.idCount() > 127) {
      throw new RuntimeException(
        "Property count greater than available bits: Please optimize");
    }
  }

  // A BitSet has a header and long array. Even with a lazy initialization attempt
  // that took a lot of memory. Use longs instead
  private long inheritValues1, inheritValues2;
  private long hasOwnValues1, hasOwnValues2;

  private CSSValue[] activeProperties = new CSSValue[CSSProperty.idCount()];
  private Map<String, CSSValue> customProperties;
  private boolean isReusable = true;
  private boolean isFrozen = false;

  @Override
  public void setProperty(CSSProperty property, CSSValue value) {
    if (property.hasExpansion()) {
      throw new UnsupportedOperationException("Cannot set expanded property!");
    } else if (value instanceof CSSDeferred) {
      this.isReusable = false;
    }

    addEntry(property.id(), value);
    setInheritValue(property.id(), false);
  }

  @Override
  public void setCustomProperty(String property, CSSValue value) {
    if (value instanceof CSSDeferred) {
      this.isReusable = false;
    }

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
    if (property.hasExpansion()) {
      for (CSSProperty expansion: property.getExpansions()) {
        unsetProperty(expansion);
      }
    } else {
      removeEntry(property.id());
      setInheritValue(property.id(), false);
    }
  }

  @Override
  public CSSValue getProperty(
    PropertyContainer parent,
    CSSProperty property
  ) {
    if (property.hasExpansion()) {
      throw new UnsupportedOperationException("Cannot get expanded property!");
    }

    int id = property.id();
    if (getHasOwnValue(id)) {
      return scanValue(id);
    }

    return parent != null && (property.inherited() || getInheritValue(id)) ?
      parent.get(property) :
      property.initial();
  }

  @Override
  public CSSValue getCustom(String property) {
    if (
      customProperties == null
      || !customProperties.containsKey(property)
    ) {
      return CSSFailure.UNSET_CUSTOM_PROPERTY;
    }

    return customProperties.get(property);
  }

  @Override
  public boolean shouldInherit(CSSProperty property) {
    return
     (property.inherited() || getInheritValue(property.id()))
      && !getHasOwnValue(property.id());
  }

  @Override
  public boolean isReusable() {
    return this.isReusable;
  }

  @Override
  public void freeze() {
    int numProperties = Long.bitCount(hasOwnValues1) + Long.bitCount(hasOwnValues2);
    CSSValue[] denseProperties = new CSSValue[numProperties];
    int i = 0;
    
    long bits1 = hasOwnValues1;
    while (bits1 != 0) {
      int id = Long.numberOfTrailingZeros(bits1);
      denseProperties[i++] = activeProperties[id];
      bits1 &= (bits1 - 1);
    }
    
    long bits2 = hasOwnValues2;
    while (bits2 != 0) {
      int id = 64 + Long.numberOfTrailingZeros(bits2);
      denseProperties[i++] = activeProperties[id];
      bits2 &= (bits2 - 1);
    }
    
    this.activeProperties = denseProperties;
    this.isFrozen=  true;
  }

  private CSSValue scanValue(int id) {
    if (!getHasOwnValue(id)) return null;
    if (!isFrozen) return activeProperties[id];
    int listPos = getPropertyPos(id);
    return activeProperties[listPos];
  }
  
  private void addEntry(int id, CSSValue value) {
    ensureNotFrozen();
    assert value != null;
    setHasOwnValue(id, true);
    activeProperties[id] = value;
  }

  private void removeEntry(int id) {
    ensureNotFrozen();
    if (!getHasOwnValue(id)) return;
    setHasOwnValue(id, false);
  }

  private void ensureNotFrozen() {
    if (isFrozen) {
      throw new IllegalStateException("Attempt to mutate frozen active styles!");
    }
  }

  private void lazilyInitCustomProperties() {
    if (customProperties == null) {
      this.customProperties = new HashMap<>(4);
    }
  }

  private int getPropertyPos(int id) {
    long mask1 = id < 64 ? (1L << id) - 1 : -1L;
    long mask2 = id < 64 ? 0 : (1L << Math.min(id - 64, 63)) - 1;
    return Long.bitCount(hasOwnValues1 & mask1) + Long.bitCount(hasOwnValues2 & mask2);
  }

  private void setHasOwnValue(int id, boolean b) {
    boolean isLowerByte = id < 64;
    if (b && isLowerByte) {
      hasOwnValues1 |= (1L << id);
    } else if (!b && isLowerByte) {
      hasOwnValues1 &= ~(1L << id);
    } else if (b) {
      hasOwnValues2 |= (1L << (id - 64));
    } else {
      hasOwnValues2 &= ~(1L << (id - 64));
    }
  }

  private boolean getHasOwnValue(int id) {
    boolean isLowerByte = id < 64;
    if (isLowerByte) {
      return (hasOwnValues1 & (1L << id)) != 0;
    } else {
      return (hasOwnValues2 & (1L << (id - 64))) != 0;
    }
  }

  private void setInheritValue(int id, boolean b) {
    boolean isLowerByte = id < 64;
    if (b && isLowerByte) {
      inheritValues1 |= (1L << id);
    } else if (!b && isLowerByte) {
      inheritValues1 &= ~(1L << id);
    } else if (b) {
      inheritValues2 |= (1L << (id - 64));
    } else {
      inheritValues2 &= ~(1L << (id - 64));
    }
  }

  private boolean getInheritValue(int id) {
    boolean isLowerByte = id < 64;
    if (isLowerByte) {
      return (inheritValues1 & (1L << id)) != 0;
    } else {
      return (inheritValues2 & (1L << (id - 64))) != 0;
    }
  }
  
}
