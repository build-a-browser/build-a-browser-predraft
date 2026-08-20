package net.buildabrowser.babbrowser.css.engine.styles.imp;

public abstract class SparsePropertyHolderWithInherit extends SparsePropertyHolder {

  private long inheritValues1, inheritValues2;

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
    if (!(o instanceof SparsePropertyHolderWithInherit other)) return false;

    if (!super.abstractEquals(other)) return false;

    return
      this.inheritValues1 == other.inheritValues1
      && this.inheritValues2 == other.inheritValues2;
  }

  protected int abstractHashCode() {
    int hashCode = super.hashCode();
    hashCode = 32 * hashCode + Long.hashCode(inheritValues1);
    hashCode = 31 * hashCode + Long.hashCode(inheritValues2);

    return hashCode;
  }

}
