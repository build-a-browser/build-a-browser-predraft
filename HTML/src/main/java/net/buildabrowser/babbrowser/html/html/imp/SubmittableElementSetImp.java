package net.buildabrowser.babbrowser.html.html.imp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import net.buildabrowser.babbrowser.html.html.FormAssociatedElement;
import net.buildabrowser.babbrowser.html.html.SubmittableElementSet;

public class SubmittableElementSetImp implements SubmittableElementSet {

  private final List<WeakReference<FormAssociatedElement>> submittableElements
    = new ArrayList<>();

  @Override
  public List<FormAssociatedElement> elements() {
    List<FormAssociatedElement> elementList = new ArrayList<>();
    // TODO: Sort by document tree order
    for (WeakReference<FormAssociatedElement> elementRef: submittableElements) {
      FormAssociatedElement element = elementRef.get();
      if (element == null) continue;
      elementList.add(element);
    }
    return elementList;
  }

  @Override
  public void addElement(FormAssociatedElement element) {
    submittableElements.add(new WeakReference<>(element));
  }

  @Override
  public void removeElement(FormAssociatedElement element) {
    submittableElements.removeIf(e -> e.get().equals(element));
  }
  
}
