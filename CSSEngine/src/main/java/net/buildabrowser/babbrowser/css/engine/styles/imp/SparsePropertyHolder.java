package net.buildabrowser.babbrowser.css.engine.styles.imp;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiConsumer;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;

public abstract class SparsePropertyHolder {

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
  private boolean isFrozen = false;

  private int rollingHash = 0;

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
    this.isFrozen = true;
  }


  public void forEachSet(BiConsumer<CSSProperty, CSSValue> itFunc) {
    int i = 0;

    long bits1 = hasOwnValues1;
    while (bits1 != 0) {
      int id = Long.numberOfTrailingZeros(bits1);
      CSSValue value = isFrozen ? activeProperties[i++] : activeProperties[id];
      itFunc.accept(CSSProperty.getById(id), value);
      bits1 &= (bits1 - 1);
    }

    long bits2 = hasOwnValues2;
    while (bits2 != 0) {
      int id = 64 + Long.numberOfTrailingZeros(bits2);
      CSSValue value = isFrozen ? activeProperties[i++] : activeProperties[id];
      itFunc.accept(CSSProperty.getById(id), value);
      bits2 &= (bits2 - 1);
    }
  }

  public void forEachInherited(BiConsumer<CSSProperty, Boolean> itFunc) {
    long bits1 = inheritValues1;
    while (bits1 != 0) {
      int id = Long.numberOfTrailingZeros(bits1);
      bits1 &= (bits1 - 1);
      if (getHasOwnValue(id)) continue;
      itFunc.accept(CSSProperty.getById(id), true);
    }

    long bits2 = inheritValues2;
    while (bits2 != 0) {
      int id = 64 + Long.numberOfTrailingZeros(bits2);
      bits2 &= (bits2 - 1);
      if (getHasOwnValue(id)) continue;
      itFunc.accept(CSSProperty.getById(id), true);
    }

    for (CSSProperty property: CSSProperty.values()) {
      if (property.hasExpansion()) continue;
      if (!property.inherited()) continue;
      if (getInheritValue(property.id())) continue;
      if (getHasOwnValue(property.id())) continue;
      itFunc.accept(property, false);
    }
  }

  protected CSSValue scanValue(int id) {
    if (!getHasOwnValue(id)) return null;
    if (!isFrozen) return activeProperties[id];
    int listPos = getPropertyPos(id);
    return activeProperties[listPos];
  }
  
  protected void addEntry(int id, CSSValue value) {
    ensureNotFrozen();
    assert value != null;
    setHasOwnValue(id, true);
    activeProperties[id] = value;
    rollingHash = 31 * rollingHash + Objects.hashCode(value);
  }

  protected void removeEntry(int id) {
    ensureNotFrozen();
    if (!getHasOwnValue(id)) return;
    setHasOwnValue(id, false);
  }

  protected void ensureNotFrozen() {
    if (isFrozen) {
      throw new IllegalStateException("Attempt to mutate frozen active styles!");
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

  protected boolean getHasOwnValue(int id) {
    boolean isLowerByte = id < 64;
    if (isLowerByte) {
      return (hasOwnValues1 & (1L << id)) != 0;
    } else {
      return (hasOwnValues2 & (1L << (id - 64))) != 0;
    }
  }

  protected void setInheritValue(int id, boolean b) {
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

  protected boolean getInheritValue(int id) {
    boolean isLowerByte = id < 64;
    if (isLowerByte) {
      return (inheritValues1 & (1L << id)) != 0;
    } else {
      return (inheritValues2 & (1L << (id - 64))) != 0;
    }
  }
  
  protected boolean abstractEquals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SparsePropertyHolder other)) return false;

    if (
      this.inheritValues1 != other.inheritValues1
      || this.inheritValues2 != other.inheritValues2
      || this.hasOwnValues1 != other.hasOwnValues1
      || this.hasOwnValues2 != other.hasOwnValues2
    ) {
      return false;
    }

    if (!(this.isFrozen && other.isFrozen)) return false;
    return Arrays.equals(this.activeProperties, other.activeProperties);
  }

  protected int abstractHashCode() {
    if (!isFrozen) {
      return super.hashCode();
    }

    int hashCode = rollingHash;
    hashCode = 32 * hashCode + Long.hashCode(inheritValues1);
    hashCode = 31 * hashCode + Long.hashCode(inheritValues2);
    hashCode = 31 * hashCode + Long.hashCode(hasOwnValues1);
    hashCode = 31 * hashCode + Long.hashCode(hasOwnValues2);

    return hashCode;
  }

}
