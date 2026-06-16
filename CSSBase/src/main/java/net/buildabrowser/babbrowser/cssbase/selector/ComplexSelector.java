package net.buildabrowser.babbrowser.cssbase.selector;

import java.util.List;

import net.buildabrowser.babbrowser.common.datastruct.SlotItem;
import net.buildabrowser.babbrowser.common.datastruct.Slottable;

public class ComplexSelector implements Slottable {

  private final List<SelectorPart> parts;
  
  private SlotItem<?> slots;

  public ComplexSelector(List<SelectorPart> parts) {
    this.parts = parts;
  }

  public List<SelectorPart> parts() {
    return parts;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ComplexSelector)) return false;

    return parts.equals(((ComplexSelector) o).parts());
  }

  public static ComplexSelector create(List<SelectorPart> parts) {
    return new ComplexSelector(parts);
  }

  @Override
  public void setSlots(SlotItem<?> slots) {
    this.slots = slots;
  }

  @Override
  public SlotItem<?> slots() {
    return this.slots;
  }

}
