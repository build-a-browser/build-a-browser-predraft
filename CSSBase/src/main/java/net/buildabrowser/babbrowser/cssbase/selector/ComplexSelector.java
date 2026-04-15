package net.buildabrowser.babbrowser.cssbase.selector;

import java.util.List;

public class ComplexSelector {

  private final List<SelectorPart> parts;

  private Object dataSlot; // TODO: I don't really like this...

  public ComplexSelector(List<SelectorPart> parts) {
    this.parts = parts;
  }

  public List<SelectorPart> parts() {
    return parts;
  }

  public Object dataSlot() {
    return this.dataSlot;
  }

  public void setDataSlot(Object dataSlot) {
    this.dataSlot = dataSlot;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ComplexSelector)) return false;

    return parts.equals(((ComplexSelector) o).parts());
  }

  public static ComplexSelector create(List<SelectorPart> parts) {
    return new ComplexSelector(parts);
  }

}
